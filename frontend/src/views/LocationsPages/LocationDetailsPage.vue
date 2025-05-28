<template>
	<template-page
		:headline="location?.name || 'Standort-Details'"
		:leftFooterButton="leftFooterButton"
		:rightFooterButton="rightFooterButton"
		@leftFooterButtonClicked="handleDelete"
		@rightFooterButtonClicked="handleEdit">
		<div class="details-container">
			<!-- Loading State -->
			<div v-if="isLoading" class="loading-container">
				<ion-spinner name="crescent" color="primary"></ion-spinner>
				<p>Lade Standort...</p>
			</div>

			<!-- Error State -->
			<div v-else-if="error && !location" class="empty-state">
				<ion-icon :icon="alertCircleOutline" class="empty-icon"></ion-icon>
				<h2>Fehler beim Laden</h2>
				<p>{{ error }}</p>
				<ion-button @click="loadLocation">
					<ion-icon :icon="refreshOutline" slot="start"></ion-icon>
					Erneut versuchen
				</ion-button>
			</div>

			<!-- Location Details -->
			<div v-else-if="location" class="content-wrapper">
				<!-- Location Header -->
				<div class="location-header">
					<div class="header-content">
						<ion-icon :icon="locationOutline" class="location-icon"></ion-icon>
						<div class="header-text">
							<h1>{{ location.name }}</h1>
							<div class="location-info">
								<div class="info-item">
									<ion-icon
										:icon="businessOutline"
										class="info-icon"></ion-icon>
									{{ location.building }}
								</div>
								<div class="info-item">
									<ion-icon :icon="layersOutline" class="info-icon"></ion-icon>
									Etage {{ location.floor }}
								</div>
								<div class="info-item">
									<ion-icon :icon="homeOutline" class="info-icon"></ion-icon>
									Raum {{ location.room }}
								</div>
							</div>
						</div>
					</div>
					<div class="quick-actions">
						<ion-button fill="clear" size="small" @click="handleEdit">
							<ion-icon :icon="createOutline" slot="icon-only"></ion-icon>
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
					<h3>Standort-Details</h3>
					<div class="metadata-grid">
						<div class="metadata-item">
							<ion-icon
								:icon="businessOutline"
								class="metadata-icon"></ion-icon>
							<div class="metadata-content">
								<span class="metadata-label">Gebäude</span>
								<span class="metadata-value">{{ location.building }}</span>
							</div>
						</div>
						<div class="metadata-item">
							<ion-icon :icon="layersOutline" class="metadata-icon"></ion-icon>
							<div class="metadata-content">
								<span class="metadata-label">Etage</span>
								<span class="metadata-value">{{ location.floor }}</span>
							</div>
						</div>
						<div class="metadata-item">
							<ion-icon :icon="homeOutline" class="metadata-icon"></ion-icon>
							<div class="metadata-content">
								<span class="metadata-label">Raum</span>
								<span class="metadata-value">{{ location.room }}</span>
							</div>
						</div>
						<div class="metadata-item">
							<ion-icon :icon="timeOutline" class="metadata-icon"></ion-icon>
							<div class="metadata-content">
								<span class="metadata-label">Erstellt am</span>
								<span class="metadata-value">{{
									formatDate(location.createdAt)
								}}</span>
							</div>
						</div>
					</div>
				</div>

				<!-- Items Section -->
				<div class="items-section">
					<div class="section-header">
						<h3>Gegenstände an diesem Standort</h3>
						<ion-chip color="primary">
							<ion-icon :icon="bagOutline" class="chip-icon"></ion-icon>
							{{ itemsAtLocation.length }}
						</ion-chip>
					</div>

					<!-- Items Loading State -->
					<div v-if="itemsLoading" class="loading-container">
						<ion-spinner name="crescent" color="primary"></ion-spinner>
						<p>Lade Gegenstände...</p>
					</div>

					<!-- No Items State -->
					<div v-else-if="itemsAtLocation.length === 0" class="empty-state">
						<ion-icon :icon="bagOutline" class="empty-icon"></ion-icon>
						<h4>Keine Gegenstände gefunden</h4>
						<p>An diesem Standort wurden noch keine Gegenstände gemeldet.</p>
						<ion-button fill="outline" @click="navigateToReportItem">
							<ion-icon :icon="addOutline" slot="start"></ion-icon>
							Gegenstand melden
						</ion-button>
					</div>

					<!-- Items Grid -->
					<div v-else class="items-grid">
						<ion-card
							v-for="(item, index) in itemsAtLocation"
							:key="item.id"
							class="item-card"
							:style="{ '--animation-delay': `${index * 0.1}s` }"
							@click="navigateToItem(item.id)">
							<ion-card-header>
								<div class="item-header">
									<ion-card-title class="item-name">{{
										item.name
									}}</ion-card-title>
									<ion-chip
										:color="getStatusColor(item.status)"
										class="status-chip">
										<ion-icon
											:icon="getStatusIcon(item.status)"
											class="chip-icon"></ion-icon>
										{{ getStatusText(item.status) }}
									</ion-chip>
								</div>
							</ion-card-header>
							<ion-card-content>
								<p v-if="item.description" class="item-description">
									{{ item.description }}
								</p>
								<div class="item-metadata">
									<div class="metadata-row">
										<ion-icon
											:icon="timeOutline"
											class="item-metadata-icon"></ion-icon>
										{{ formatDate(item.createdAt) }}
									</div>
								</div>
							</ion-card-content>
							<div class="item-actions">
								<ion-button
									fill="clear"
									size="small"
									@click.stop="navigateToItem(item.id)">
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
			@didDismiss="showDeleteAlert = false"></ion-alert>
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
	handRightOutline,
	checkmarkCircleOutline,
	closeCircleOutline,
	alertCircleOutline,
	eyeOffOutline,
	flagOutline,
	checkmarkOutline,
} from 'ionicons/icons';
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useLocationStore } from '@/stores/locationStore';
import type { Location } from '@/models/location';
import type { Item } from '@/models/item';

const router = useRouter();
const route = useRoute();
const locationStore = useLocationStore();

// State
const location = ref<Location | null>(null);
const itemsAtLocation = ref<Item[]>([]);
const showDeleteAlert = ref(false);
const itemsLoading = ref(false);

// Computed
const isLoading = computed(() => locationStore.isLoading && !location.value);
const error = computed(() => locationStore.getError);

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

// Methods
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
	switch (status.toUpperCase()) {
		case 'FOUND':
			return 'success';
		case 'CLAIMED':
			return 'medium';
		case 'LOST':
			return 'warning';
		default:
			return 'primary';
	}
};

const getStatusIcon = (status: string) => {
	switch (status.toUpperCase()) {
		case 'LOST':
			return eyeOffOutline;
		case 'FOUND':
			return eyeOutline;
		case 'CLAIMED':
			return checkmarkOutline;
		default:
			return flagOutline;
	}
};

const getStatusText = (status: string) => {
	switch (status.toUpperCase()) {
		case 'FOUND':
			return 'Gefunden';
		case 'CLAIMED':
			return 'Abgeholt';
		case 'LOST':
			return 'Verloren';
		default:
			return status;
	}
};

const loadLocation = async () => {
	try {
		const locationId = Number(route.params.id);
		if (!locationId) {
			throw new Error('Invalid location ID');
		}

		const loadedLocation = await locationStore.fetchLocationById(locationId);
		if (loadedLocation) {
			location.value = loadedLocation;
			await loadItemsAtLocation(locationId);
		} else {
			throw new Error('Location not found');
		}
	} catch (error) {
		console.error('Error loading location:', error);
		// Navigate back if location cannot be loaded
		router.back();
	}
};

const loadItemsAtLocation = async (locationId: number) => {
	try {
		itemsLoading.value = true;
		const items = await locationStore.fetchItemsByLocation(locationId);
		itemsAtLocation.value = items;
	} catch (error) {
		console.error('Error loading items at location:', error);
		itemsAtLocation.value = [];
	} finally {
		itemsLoading.value = false;
	}
};

const handleEdit = () => {
	if (location.value) {
		router.push(`/locations/${location.value.id}/edit`);
	}
};

const handleDelete = () => {
	showDeleteAlert.value = true;
};

const confirmDelete = async () => {
	if (!location.value) return;

	try {
		await locationStore.deleteLocation(location.value.id);
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

.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	text-align: center;
	padding: 60px 20px;
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

.content-wrapper {
	padding: 20px;
	animation: fadeInUp 0.6s ease-out;
}

.location-header {
	background: linear-gradient(
		135deg,
		var(--ion-color-primary),
		var(--ion-color-primary-shade)
	);
	color: white;
	padding: 24px;
	border-radius: 16px;
	margin-bottom: 24px;
	box-shadow: 0 8px 24px rgba(var(--ion-color-primary-rgb), 0.3);
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	position: relative;
	overflow: hidden;
}

.location-header::before {
	content: '';
	position: absolute;
	top: -50%;
	right: -50%;
	width: 100%;
	height: 200%;
	background: linear-gradient(
		45deg,
		transparent,
		rgba(255, 255, 255, 0.1),
		transparent
	);
	transform: rotate(45deg);
	transition: all 0.6s ease;
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
	grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
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

.empty-state h4 {
	color: var(--ion-color-dark);
	margin: 0 0 8px 0;
	font-weight: 600;
}

.items-grid {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
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
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
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
		transform: translateY(40px);
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
	}

	.header-content {
		flex-direction: column;
		gap: 12px;
	}

	.quick-actions {
		align-self: flex-end;
		margin-top: -12px;
	}

	.location-info {
		gap: 6px;
	}

	.metadata-grid {
		grid-template-columns: 1fr;
	}

	.section-header {
		flex-direction: column;
		align-items: flex-start;
		gap: 8px;
	}

	.items-grid {
		grid-template-columns: 1fr;
	}
}

@media (max-width: 480px) {
	.header-content {
		gap: 8px;
	}

	.location-info {
		gap: 4px;
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
		grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
	}

	.metadata-grid {
		grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
	}
}
</style>
