<template>
	<template-page
		:headline="location.name || 'Standort-Details'"
		:leftFooterButton="leftFooterButton"
		:rightFooterButton="rightFooterButton"
		@leftFooterButtonClicked="handleDelete"
		@rightFooterButtonClicked="handleEdit">
		
		<div class="details-container">
			<!-- Loading State -->
			<div v-if="isLoading" class="loading-container">
				<ion-spinner name="crescent" color="primary"></ion-spinner>
				<p>Lade Standort-Details...</p>
			</div>

			<div v-else class="content-wrapper">
				<!-- Location Header -->
				<div class="location-header">
					<div class="header-content">
						<ion-icon :icon="locationOutline" class="location-icon"></ion-icon>
						<div class="header-text">
							<h1>{{ location.name }}</h1>
							<div class="location-info">
								<div class="info-item">
									<ion-icon :icon="businessOutline" class="info-icon"></ion-icon>
									<span>Gebäude {{ location.building }}</span>
								</div>
								<div class="info-item">
									<ion-icon :icon="layersOutline" class="info-icon"></ion-icon>
									<span>{{ location.floor }}. Etage</span>
								</div>
								<div class="info-item">
									<ion-icon :icon="homeOutline" class="info-icon"></ion-icon>
									<span>Raum {{ location.room }}</span>
								</div>
							</div>
						</div>
					</div>
					
					<!-- Quick Actions -->
					<div class="quick-actions">
						<ion-button fill="clear" size="small" @click="handleEdit">
							<ion-icon :icon="createOutline" slot="icon-only"></ion-icon>
						</ion-button>
						<ion-button fill="clear" size="small" color="danger" @click="handleDelete">
							<ion-icon :icon="trashOutline" slot="icon-only"></ion-icon>
						</ion-button>
					</div>
				</div>

				<!-- Description Section -->
				<div v-if="location.description" class="description-section">
					<h3>Beschreibung</h3>
					<p>{{ location.description }}</p>
				</div>

				<!-- Metadata Section -->
				<div class="metadata-section">
					<h3>Details</h3>
					<div class="metadata-grid">
						<div class="metadata-item">
							<ion-icon :icon="timeOutline" class="metadata-icon"></ion-icon>
							<div class="metadata-content">
								<span class="metadata-label">Erstellt</span>
								<span class="metadata-value">{{ formatDate(location.createdAt) }}</span>
							</div>
						</div>
						<div class="metadata-item" v-if="location.updatedAt !== location.createdAt">
							<ion-icon :icon="refreshOutline" class="metadata-icon"></ion-icon>
							<div class="metadata-content">
								<span class="metadata-label">Zuletzt geändert</span>
								<span class="metadata-value">{{ formatDate(location.updatedAt) }}</span>
							</div>
						</div>
					</div>
				</div>

				<!-- Items at Location Section -->
				<div class="items-section">
					<div class="section-header">
						<h3>Gegenstände an diesem Standort</h3>
						<ion-chip color="primary" outline>
							<ion-icon :icon="bagOutline" class="chip-icon"></ion-icon>
							{{ itemsAtLocation.length }}
						</ion-chip>
					</div>

					<div v-if="itemsAtLocation.length === 0" class="empty-state">
						<ion-icon :icon="bagOutline" class="empty-icon"></ion-icon>
						<h4>Keine Gegenstände gefunden</h4>
						<p>An diesem Standort wurden noch keine Gegenstände gemeldet.</p>
						<ion-button 
							fill="outline" 
							color="primary" 
							@click="navigateToReportItem"
							class="ion-margin-top"
						>
							<ion-icon :icon="addOutline" slot="start"></ion-icon>
							Gegenstand melden
						</ion-button>
					</div>

					<div v-else class="items-grid">
						<ion-card
							v-for="(item, index) in itemsAtLocation"
							:key="item.id"
							class="item-card"
							:style="{ '--animation-delay': `${index * 0.1}s` }"
							@click="navigateToItem(item.id)"
							button
						>
							<ion-card-header>
								<div class="item-header">
									<ion-card-title class="item-name">
										{{ item.name }}
									</ion-card-title>
									<ion-chip 
										:color="getStatusColor(item.status)" 
										class="status-chip"
									>
										{{ getStatusText(item.status) }}
									</ion-chip>
								</div>
							</ion-card-header>
							
							<ion-card-content>
								<p class="item-description">{{ item.description }}</p>
								
								<div class="item-metadata">
									<div class="metadata-row">
										<ion-icon :icon="timeOutline" class="item-metadata-icon"></ion-icon>
										<span>{{ formatDate(item.reportedAt) }}</span>
									</div>
								</div>
							</ion-card-content>

							<div class="item-actions">
								<ion-button 
									fill="clear" 
									size="small" 
									color="primary"
									@click.stop="navigateToItem(item.id)"
								>
									<ion-icon :icon="eyeOutline" slot="start"></ion-icon>
									Details
								</ion-button>
							</div>
						</ion-card>
					</div>
				</div>
			</div>
		</div>

		<!-- Delete Confirmation Modal -->
		<ion-alert
			:is-open="showDeleteAlert"
			header="Standort löschen"
			message="Bist du sicher, dass du diesen Standort löschen möchtest? Diese Aktion kann nicht rückgängig gemacht werden."
			:buttons="alertButtons"
			@didDismiss="showDeleteAlert = false"
		></ion-alert>
	</template-page>
</template>

<script setup lang="ts">
import TemplatePage from '@/components/TemplatePage.vue';
import {
	IonIcon,
	IonButton,
	IonChip,
	IonCard,
	IonCardHeader,
	IonCardTitle,
	IonCardContent,
	IonSpinner,
	IonAlert,
} from '@ionic/vue';
import {
	locationOutline,
	businessOutline,
	layersOutline,
	homeOutline,
	createOutline,
	trashOutline,
	timeOutline,
	refreshOutline,
	bagOutline,
	addOutline,
	eyeOutline,
	checkmarkCircleOutline,
	closeCircleOutline,
} from 'ionicons/icons';
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import type { Location } from '@/models/location';

const router = useRouter();
const route = useRoute();

const location = ref<Location>({
	id: 1,
	name: '',
	description: '',
	building: '',
	floor: '',
	room: '',
	createdAt: '',
	updatedAt: '',
});

const itemsAtLocation = ref([
	{ 
		id: 1, 
		name: 'Schwarzer Rucksack', 
		description: 'Schwarzer Rucksack mit Laptop-Fach und mehreren Taschen',
		status: 'found',
		reportedAt: '2024-01-15T10:30:00Z'
	},
	{ 
		id: 2, 
		name: 'Blaue Wasserflasche', 
		description: 'Blaue Metallflasche mit Universitäts-Logo',
		status: 'claimed',
		reportedAt: '2024-01-14T14:20:00Z'
	},
	{ 
		id: 3, 
		name: 'Rote Jacke', 
		description: 'Rote Winterjacke, Größe M, Marke North Face',
		status: 'found',
		reportedAt: '2024-01-13T16:45:00Z'
	},
]);

const isLoading = ref(true);
const showDeleteAlert = ref(false);

const leftFooterButton = computed(() => ({
	name: 'Löschen',
	color: 'danger',
	icon: trashOutline,
}));

const rightFooterButton = computed(() => ({
	name: 'Bearbeiten',
	color: 'primary',
	icon: createOutline,
}));

const alertButtons = [
	{
		text: 'Abbrechen',
		role: 'cancel',
		cssClass: 'alert-button-cancel',
	},
	{
		text: 'Löschen',
		role: 'destructive',
		cssClass: 'alert-button-confirm',
		handler: () => confirmDelete(),
	},
];

const formatDate = (dateString: string) => {
	return new Date(dateString).toLocaleDateString('de-DE', {
		day: '2-digit',
		month: '2-digit',
		year: 'numeric',
		hour: '2-digit',
		minute: '2-digit',
	});
};

const getStatusColor = (status: string) => {
	switch (status) {
		case 'found': return 'success';
		case 'claimed': return 'medium';
		case 'lost': return 'warning';
		default: return 'primary';
	}
};

const getStatusText = (status: string) => {
	switch (status) {
		case 'found': return 'Gefunden';
		case 'claimed': return 'Abgeholt';
		case 'lost': return 'Verloren';
		default: return 'Unbekannt';
	}
};

const loadLocation = async () => {
	isLoading.value = true;
	
	try {
		const locationId = route.params.id as string;
		
		// TODO: Replace with actual API call
		console.log('Loading location with ID:', locationId);
		
		// Simulate API call
		await new Promise(resolve => setTimeout(resolve, 1000));
		
		// Mock data - replace with actual API data
		location.value = {
			id: Number(locationId),
			name: 'Hauptbibliothek',
			description: 'Zentrale Bibliothek des Campus mit über 100.000 Büchern und modernen Arbeitsplätzen',
			building: 'A',
			floor: '1',
			room: '101',
			createdAt: '2024-01-01T10:00:00Z',
			updatedAt: '2024-01-02T12:00:00Z',
		};
	} catch (error) {
		console.error('Error loading location:', error);
		// TODO: Show error toast and navigate back
		router.back();
	} finally {
		isLoading.value = false;
	}
};

const handleEdit = () => {
	router.push(`/locations/${location.value.id}/edit`);
};

const handleDelete = () => {
	showDeleteAlert.value = true;
};

const confirmDelete = async () => {
	try {
		// TODO: Implement actual delete logic
		console.log('Deleting location:', location.value.id);
		
		// Simulate API call
		await new Promise(resolve => setTimeout(resolve, 1000));
		
		// Navigate back to locations
		router.push('/locations/home');
	} catch (error) {
		console.error('Error deleting location:', error);
		// TODO: Show error toast
	}
};

const navigateToItem = (itemId: number) => {
	router.push(`/items/${itemId}`);
};

const navigateToReportItem = () => {
	router.push('/items/report');
};

onMounted(() => {
	loadLocation();
});
</script>

<style scoped>
.details-container {
	min-height: 100vh;
	background: var(--ion-color-light-tint);
}

.loading-container {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 60px 20px;
	text-align: center;
}

.loading-container p {
	margin-top: 16px;
	color: var(--ion-color-medium);
}

.content-wrapper {
	padding: 20px;
	animation: fadeInUp 0.6s ease-out;
}

.location-header {
	background: linear-gradient(135deg, var(--ion-color-primary), var(--ion-color-primary-shade));
	color: white;
	padding: 24px;
	border-radius: 16px;
	margin-bottom: 24px;
	box-shadow: 0 8px 24px rgba(var(--ion-color-primary-rgb), 0.3);
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
}

.header-content {
	display: flex;
	align-items: flex-start;
	gap: 16px;
	flex: 1;
}

.location-icon {
	font-size: 40px;
	opacity: 0.9;
	margin-top: 4px;
}

.header-text h1 {
	color: white;
	margin: 0 0 12px 0;
	font-size: 1.5em;
	font-weight: 600;
}

.location-info {
	display: flex;
	flex-direction: column;
	gap: 8px;
}

.info-item {
	display: flex;
	align-items: center;
	gap: 8px;
	color: rgba(255, 255, 255, 0.9);
	font-size: 0.9em;
}

.info-icon {
	font-size: 16px;
	opacity: 0.8;
}

.quick-actions {
	display: flex;
	gap: 8px;
}

.description-section,
.metadata-section,
.items-section {
	background: white;
	border-radius: 12px;
	padding: 20px;
	margin-bottom: 16px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.description-section h3,
.metadata-section h3,
.items-section h3 {
	color: var(--ion-color-dark);
	margin: 0 0 16px 0;
	font-size: 1.1em;
	font-weight: 600;
}

.description-section p {
	color: var(--ion-color-medium-shade);
	line-height: 1.6;
	margin: 0;
}

.metadata-grid {
	display: grid;
	gap: 16px;
}

.metadata-item {
	display: flex;
	align-items: center;
	gap: 12px;
	padding: 12px;
	background: var(--ion-color-light-tint);
	border-radius: 8px;
}

.metadata-icon {
	font-size: 20px;
	color: var(--ion-color-primary);
}

.metadata-content {
	display: flex;
	flex-direction: column;
}

.metadata-label {
	font-size: 0.8em;
	color: var(--ion-color-medium);
	font-weight: 500;
}

.metadata-value {
	color: var(--ion-color-dark);
	font-weight: 600;
}

.section-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 16px;
}

.chip-icon {
	font-size: 14px;
	margin-right: 4px;
}

.empty-state {
	text-align: center;
	padding: 40px 20px;
}

.empty-icon {
	font-size: 48px;
	color: var(--ion-color-medium);
	margin-bottom: 16px;
}

.empty-state h4 {
	color: var(--ion-color-dark);
	margin: 0 0 8px 0;
	font-weight: 600;
}

.empty-state p {
	color: var(--ion-color-medium);
	margin: 0 0 20px 0;
	line-height: 1.5;
}

.items-grid {
	display: grid;
	gap: 16px;
}

.item-card {
	border-radius: 12px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
	transition: all 0.3s ease;
	cursor: pointer;
	animation: slideInUp 0.6s ease-out;
	animation-delay: var(--animation-delay);
	animation-fill-mode: backwards;
}

.item-card:hover {
	transform: translateY(-2px);
	box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.item-header {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	gap: 12px;
}

.item-name {
	color: var(--ion-color-dark);
	font-size: 1.1em;
	font-weight: 600;
	margin: 0;
	flex: 1;
}

.status-chip {
	margin: 0;
}

.item-description {
	color: var(--ion-color-medium-shade);
	line-height: 1.5;
	margin: 0 0 16px 0;
}

.item-metadata {
	padding-top: 12px;
	border-top: 1px solid var(--ion-color-light-shade);
}

.metadata-row {
	display: flex;
	align-items: center;
	gap: 6px;
	font-size: 0.85em;
	color: var(--ion-color-medium);
}

.item-metadata-icon {
	font-size: 14px;
}

.item-actions {
	padding: 8px 16px 16px;
	border-top: 1px solid var(--ion-color-light-shade);
	background: var(--ion-color-light-tint);
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
		transform: translateY(20px);
	}
	to {
		opacity: 1;
		transform: translateY(0);
	}
}

/* Responsive Design */
@media (max-width: 768px) {
	.content-wrapper {
		padding: 16px;
	}
	
	.location-header {
		flex-direction: column;
		gap: 16px;
		align-items: stretch;
	}
	
	.header-content {
		flex-direction: column;
		align-items: flex-start;
		gap: 12px;
	}
	
	.quick-actions {
		align-self: flex-end;
	}
	
	.location-info {
		flex-direction: row;
		flex-wrap: wrap;
		gap: 12px;
	}
	
	.metadata-grid {
		gap: 12px;
	}
	
	.section-header {
		flex-direction: column;
		align-items: flex-start;
		gap: 12px;
	}
}

@media (max-width: 480px) {
	.header-content {
		align-items: center;
		text-align: center;
	}
	
	.location-info {
		justify-content: center;
	}
	
	.item-header {
		flex-direction: column;
		align-items: flex-start;
		gap: 8px;
	}
	
	.status-chip {
		align-self: flex-start;
	}
}

@media (min-width: 768px) {
	.items-grid {
		grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
	}
	
	.metadata-grid {
		grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
	}
}
</style>
