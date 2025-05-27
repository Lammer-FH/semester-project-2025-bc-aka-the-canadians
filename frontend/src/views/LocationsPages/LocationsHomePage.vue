<template>
	<template-page addButtonPath="/locations/add" addButtonText="Neuen Standort hinzufügen">
		<template #header>
			<ion-toolbar>
				<navigation-tabs v-model="activeTab" class="full-width-tabs" />
			</ion-toolbar>
			<ion-toolbar>
				<ion-searchbar 
					v-model="searchTerm" 
					placeholder="Standorte durchsuchen..." 
					show-clear-button="focus"
					:debounce="300"
					class="custom-searchbar"
				/>
			</ion-toolbar>
		</template>

		<div v-if="activeTab === 'locations'" class="locations-container">
			<!-- Empty State -->
			<div v-if="filteredLocations.length === 0" class="empty-state">
				<ion-icon :icon="locationOutline" class="empty-icon"></ion-icon>
				<h2>Keine Standorte gefunden</h2>
				<p v-if="searchTerm">
					Deine Suche nach "{{ searchTerm }}" ergab keine Ergebnisse.
				</p>
				<p v-else>
					Noch keine Standorte vorhanden. Füge den ersten Standort hinzu!
				</p>
				<ion-button 
					v-if="!searchTerm" 
					fill="solid" 
					color="primary" 
					@click="$router.push('/locations/add')"
					class="ion-margin-top"
				>
					<ion-icon :icon="addOutline" slot="start"></ion-icon>
					Ersten Standort hinzufügen
				</ion-button>
			</div>

			<!-- Locations Grid -->
			<div v-else class="locations-grid">
				<ion-card
					v-for="(location, index) in filteredLocations"
					:key="location.id"
					class="location-card"
					:style="{ '--animation-delay': `${index * 0.1}s` }"
					@click="navigateToLocation(location.id)"
					button
				>
					<div class="card-header">
						<ion-card-header>
							<div class="header-content">
								<ion-card-title class="location-name">
									{{ location.name }}
								</ion-card-title>
								<ion-chip class="building-chip" color="primary" outline>
									<ion-icon :icon="businessOutline" class="chip-icon"></ion-icon>
									{{ location.building }}
								</ion-chip>
							</div>
							<ion-card-subtitle class="location-details">
								<div class="detail-item">
									<ion-icon :icon="layersOutline" class="detail-icon"></ion-icon>
									<span>{{ location.floor }}. Etage</span>
								</div>
								<div class="detail-item">
									<ion-icon :icon="homeOutline" class="detail-icon"></ion-icon>
									<span>Raum {{ location.room }}</span>
								</div>
							</ion-card-subtitle>
						</ion-card-header>
					</div>

					<ion-card-content class="card-content">
						<p class="description">{{ location.description }}</p>
						
						<div class="metadata">
							<div class="metadata-item">
								<ion-icon :icon="timeOutline" class="metadata-icon"></ion-icon>
								<span class="metadata-text">
									Erstellt: {{ formatDate(location.createdAt) }}
								</span>
							</div>
							<div class="metadata-item" v-if="location.updatedAt !== location.createdAt">
								<ion-icon :icon="refreshOutline" class="metadata-icon"></ion-icon>
								<span class="metadata-text">
									Aktualisiert: {{ formatDate(location.updatedAt) }}
								</span>
							</div>
						</div>
					</ion-card-content>

					<!-- Action Buttons -->
					<div class="card-actions">
						<ion-button 
							fill="clear" 
							size="small" 
							color="primary"
							@click.stop="navigateToLocation(location.id)"
						>
							<ion-icon :icon="eyeOutline" slot="start"></ion-icon>
							Details
						</ion-button>
						<ion-button 
							fill="clear" 
							size="small" 
							color="medium"
							@click.stop="editLocation(location.id)"
						>
							<ion-icon :icon="createOutline" slot="start"></ion-icon>
							Bearbeiten
						</ion-button>
					</div>
				</ion-card>
			</div>

			<!-- Loading State -->
			<div v-if="isLoading" class="loading-container">
				<ion-spinner name="crescent" color="primary"></ion-spinner>
				<p>Lade Standorte...</p>
			</div>
		</div>
	</template-page>
</template>

<script setup lang="ts">
import TemplatePage from '@/components/TemplatePage.vue';
import NavigationTabs from '@/components/NavigationTabs.vue';
import {
	IonSearchbar,
	IonCard,
	IonCardHeader,
	IonCardTitle,
	IonCardSubtitle,
	IonCardContent,
	IonButton,
	IonIcon,
	IonChip,
	IonSpinner,
} from '@ionic/vue';
import {
	locationOutline,
	addOutline,
	businessOutline,
	layersOutline,
	homeOutline,
	timeOutline,
	refreshOutline,
	eyeOutline,
	createOutline,
} from 'ionicons/icons';
import { ref, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import type { Location } from '@/models/location';

const router = useRouter();
const activeTab = ref('locations');
const searchTerm = ref('');
const isLoading = ref(false);

// Replace mock data with actual data later on
const locations = ref<Location[]>([
	{
		id: 1,
		name: 'Bibliothek',
		description: 'Hauptbibliothek des Campus mit umfangreicher Sammlung',
		building: 'A',
		floor: '1',
		room: '101',
		createdAt: '2024-01-01T10:00:00Z',
		updatedAt: '2024-01-02T12:00:00Z',
	},
	{
		id: 2,
		name: 'Cafeteria',
		description: 'Studentencafeteria mit vielfältigem Angebot',
		building: 'B',
		floor: '2',
		room: '201',
		createdAt: '2024-01-03T09:00:00Z',
		updatedAt: '2024-01-04T11:00:00Z',
	},
	{
		id: 3,
		name: 'Sporthalle',
		description: 'Sport- und Fitnesszentrum für alle Studierenden',
		building: 'C',
		floor: '3',
		room: '301',
		createdAt: '2024-01-05T08:00:00Z',
		updatedAt: '2024-01-06T10:00:00Z',
	},
	{
		id: 4,
		name: 'Computerlabor',
		description: 'Hochmodernes Computerlabor mit neuester Technik',
		building: 'A',
		floor: '2',
		room: '205',
		createdAt: '2024-01-07T14:00:00Z',
		updatedAt: '2024-01-07T14:00:00Z',
	},
]);

const filteredLocations = computed(() =>
	locations.value.filter(
		(loc) =>
			loc.name.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
			loc.description.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
			loc.building.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
			loc.room.toLowerCase().includes(searchTerm.value.toLowerCase())
	)
);

const formatDate = (dateString: string) => {
	return new Date(dateString).toLocaleDateString('de-DE', {
		day: '2-digit',
		month: '2-digit',
		year: 'numeric',
	});
};

const navigateToLocation = (locationId: number) => {
	router.push(`/locations/${locationId}`);
};

const editLocation = (locationId: number) => {
	router.push(`/locations/${locationId}/edit`);
};

watch(activeTab, (tab) => {
	if (tab === 'items') {
		router.push('/items/home');
	}
});
</script>

<style scoped>
.locations-container {
	padding: 16px;
	min-height: 100vh;
}

.custom-searchbar {
	--background: var(--ion-color-light);
	--border-radius: 12px;
	--box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
	margin: 8px 16px;
}

.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	text-align: center;
	padding: 60px 20px;
	margin-top: 80px;
}

.empty-icon {
	font-size: 64px;
	color: var(--ion-color-medium);
	margin-bottom: 20px;
}

.empty-state h2 {
	color: var(--ion-color-dark);
	margin-bottom: 10px;
	font-weight: 600;
}

.empty-state p {
	color: var(--ion-color-medium);
	margin-bottom: 30px;
	line-height: 1.5;
}

.locations-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
	gap: 12px;
	animation: fadeInUp 0.6s ease-out;
}

.location-card {
	border-radius: 12px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
	cursor: pointer;
	overflow: hidden;
	border: 1px solid var(--ion-color-light-shade);
	animation: slideInUp 0.6s ease-out;
	animation-delay: var(--animation-delay);
	animation-fill-mode: backwards;
}

.location-card:hover {
	transform: translateY(-2px);
	box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.card-header {
	background: linear-gradient(135deg, var(--ion-color-primary-tint), var(--ion-color-primary));
	color: white;
	position: relative;
	overflow: hidden;
}

.card-header::before {
	content: '';
	position: absolute;
	top: -50%;
	right: -50%;
	width: 100%;
	height: 200%;
	background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.1), transparent);
	transform: rotate(45deg);
	transition: all 0.6s ease;
}

.location-card:hover .card-header::before {
	transform: rotate(45deg) translateX(100%);
}

.header-content {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	margin-bottom: 8px;
}

.location-name {
	color: white;
	font-size: 1.2em;
	font-weight: 600;
	margin: 0;
	flex: 1;
}

.building-chip {
	--background: rgba(255, 255, 255, 0.2);
	--color: white;
	margin-left: 12px;
}

.chip-icon {
	font-size: 14px;
	margin-right: 4px;
}

.location-details {
	display: flex;
	flex-direction: column;
	gap: 4px;
	color: rgba(255, 255, 255, 0.9);
}

.detail-item {
	display: flex;
	align-items: center;
	gap: 6px;
	font-size: 0.9em;
}

.detail-icon {
	font-size: 16px;
	opacity: 0.8;
}

.card-content {
	padding: 16px;
}

.description {
	color: var(--ion-color-dark);
	line-height: 1.4;
	margin-bottom: 12px;
	font-size: 0.9em;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
}

.metadata {
	display: flex;
	flex-direction: column;
	gap: 8px;
	margin-top: 16px;
	padding-top: 16px;
	border-top: 1px solid var(--ion-color-light-shade);
}

.metadata-item {
	display: flex;
	align-items: center;
	gap: 8px;
}

.metadata-icon {
	font-size: 14px;
	color: var(--ion-color-medium);
	flex-shrink: 0;
}

.metadata-text {
	font-size: 0.8em;
	color: var(--ion-color-medium);
}

.card-actions {
	display: flex;
	justify-content: space-between;
	padding: 8px 12px 12px;
	border-top: 1px solid var(--ion-color-light-shade);
	background: var(--ion-color-light-tint);
}

.loading-container {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 60px 20px;
}

.loading-container p {
	margin-top: 16px;
	color: var(--ion-color-medium);
}

@keyframes fadeInUp {
	from {
		opacity: 0;
		transform: translateY(30px);
	}
	to {
		opacity: 1;
		transform: translateY(0);
	}
}

@keyframes slideInUp {
	from {
		opacity: 0;
		transform: translateY(40px);
	}
	to {
		opacity: 1;
		transform: translateY(0);
	}
}

/* Responsive Design */
@media (max-width: 768px) {
	.locations-grid {
		grid-template-columns: 1fr;
		gap: 12px;
	}
	
	.locations-container {
		padding: 12px;
	}
	
	.header-content {
		flex-direction: column;
		align-items: flex-start;
		gap: 8px;
	}
	
	.building-chip {
		margin-left: 0;
	}
	
	.card-actions {
		flex-direction: column;
		gap: 8px;
	}
}

@media (min-width: 1200px) {
	.locations-grid {
		grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
	}
}
</style>
