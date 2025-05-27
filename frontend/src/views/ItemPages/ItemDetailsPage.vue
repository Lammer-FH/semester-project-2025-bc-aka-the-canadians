<template>
	<template-page
		:headline="item.name || 'Gegenstand-Details'"
		:leftFooterButton="leftFooterButton"
		:rightFooterButton="rightFooterButton"
		@leftFooterButtonClicked="handleDelete"
		@rightFooterButtonClicked="handleEdit">
		
		<div class="details-container">
			<!-- Loading State -->
			<div v-if="isLoading" class="loading-container">
				<ion-spinner name="crescent" color="primary"></ion-spinner>
				<p>Lade Gegenstand-Details...</p>
			</div>

			<div v-else class="content-wrapper">
				<!-- Item Header -->
				<div class="item-header" :class="getStatusClass(item.status)">
					<div class="header-content">
						<ion-icon :icon="getStatusIcon(item.status)" class="item-icon"></ion-icon>
						<div class="header-text">
							<h1>{{ item.name }}</h1>
							<div class="item-info">
								<div class="info-item">
									<ion-icon :icon="locationOutline" class="info-icon"></ion-icon>
									<span>{{ item.location }}</span>
								</div>
								<ion-chip :color="getStatusColor(item.status)" class="status-chip">
									{{ getStatusText(item.status) }}
								</ion-chip>
							</div>
						</div>
					</div>
					
					<!-- Quick Actions -->
					<div class="quick-actions">
						<ion-button 
							v-if="item.status === 'found'" 
							fill="clear" 
							size="small" 
							color="success"
							@click="claimItem"
						>
							<ion-icon :icon="handRightOutline" slot="icon-only"></ion-icon>
						</ion-button>
						<ion-button fill="clear" size="small" @click="handleEdit">
							<ion-icon :icon="createOutline" slot="icon-only"></ion-icon>
						</ion-button>
						<ion-button fill="clear" size="small" color="danger" @click="handleDelete">
							<ion-icon :icon="trashOutline" slot="icon-only"></ion-icon>
						</ion-button>
					</div>
				</div>

				<!-- Item Image -->
				<div v-if="item.imageUrl" class="image-section">
					<h3>Bild</h3>
					<div class="image-container">
						<img :src="item.imageUrl" :alt="item.name" class="item-image" @click="openImageModal" />
						<ion-button 
							fill="clear" 
							color="primary" 
							@click="openImageModal"
							class="view-fullsize-btn"
						>
							<ion-icon :icon="expandOutline" slot="start"></ion-icon>
							Vollbild anzeigen
						</ion-button>
					</div>
				</div>

				<!-- Description Section -->
				<div v-if="item.description" class="description-section">
					<h3>Beschreibung</h3>
					<p>{{ item.description }}</p>
				</div>

				<!-- Metadata Section -->
				<div class="metadata-section">
					<h3>Details</h3>
					<div class="metadata-grid">
						<div class="metadata-item">
							<ion-icon :icon="timeOutline" class="metadata-icon"></ion-icon>
							<div class="metadata-content">
								<span class="metadata-label">Gemeldet</span>
								<span class="metadata-value">{{ formatDate(item.createdAt) }}</span>
							</div>
						</div>
						<div class="metadata-item" v-if="item.updatedAt !== item.createdAt">
							<ion-icon :icon="refreshOutline" class="metadata-icon"></ion-icon>
							<div class="metadata-content">
								<span class="metadata-label">Zuletzt geändert</span>
								<span class="metadata-value">{{ formatDate(item.updatedAt) }}</span>
							</div>
						</div>
					</div>
				</div>

				<!-- Actions Section -->
				<div v-if="item.status === 'found'" class="actions-section">
					<h3>Aktionen</h3>
					<ion-button 
						expand="block" 
						color="success" 
						@click="claimItem"
						class="claim-button"
					>
						<ion-icon :icon="handRightOutline" slot="start"></ion-icon>
						Gegenstand abholen
					</ion-button>
				</div>
			</div>
		</div>

		<!-- Delete Confirmation Modal -->
		<ion-alert
			:is-open="showDeleteAlert"
			header="Gegenstand löschen"
			message="Bist du sicher, dass du diesen Gegenstand löschen möchtest? Diese Aktion kann nicht rückgängig gemacht werden."
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
	IonSpinner,
	IonAlert,
} from '@ionic/vue';
import {
	locationOutline,
	createOutline,
	trashOutline,
	timeOutline,
	refreshOutline,
	handRightOutline,
	checkmarkCircleOutline,
	alertCircleOutline,
	bagOutline,
	closeCircleOutline,
	expandOutline,
} from 'ionicons/icons';
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import type { Item } from '@/models/item';

const router = useRouter();
const route = useRoute();

const item = ref<Item>({
	id: 1,
	name: '',
	description: '',
	location: '',
	status: 'lost',
	createdAt: '',
	updatedAt: '',
	imageUrl: '',
});

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
		case 'lost': return 'warning';
		case 'claimed': return 'medium';
		default: return 'primary';
	}
};

const getStatusText = (status: string) => {
	switch (status) {
		case 'found': return 'Gefunden';
		case 'lost': return 'Verloren';
		case 'claimed': return 'Abgeholt';
		default: return 'Unbekannt';
	}
};

const getStatusIcon = (status: string) => {
	switch (status) {
		case 'found': return checkmarkCircleOutline;
		case 'lost': return alertCircleOutline;
		case 'claimed': return handRightOutline;
		default: return bagOutline;
	}
};

const getStatusClass = (status: string) => {
	return `status-${status}`;
};

const loadItem = async () => {
	isLoading.value = true;
	
	try {
		const itemId = route.params.id as string;
		
		// TODO: Replace with actual API call
		console.log('Loading item with ID:', itemId);
		
		// Simulate API call
		await new Promise(resolve => setTimeout(resolve, 1000));
		
		// Mock data - replace with actual API data
		item.value = {
			id: Number(itemId),
			name: 'Schwarzer Rucksack',
			description: 'Großer schwarzer Rucksack mit Laptop-Fach, Marke Eastpak. Enthält wichtige Dokumente.',
			location: 'Bibliothek',
			status: 'found',
			createdAt: '2024-01-15T10:00:00Z',
			updatedAt: '2024-01-15T10:00:00Z',
			imageUrl: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KICA8cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjMzMzIi8+CiAgPHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxOCIgZmlsbD0iI2ZmZiIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPlNjaHdhcnplciBSdWNrc2FjazwvdGV4dD4KPC9zdmc+',
		};
	} catch (error) {
		console.error('Error loading item:', error);
		// TODO: Show error toast and navigate back
		router.back();
	} finally {
		isLoading.value = false;
	}
};

const handleEdit = () => {
	router.push(`/items/${item.value.id}/edit`);
};

const handleDelete = () => {
	showDeleteAlert.value = true;
};

const confirmDelete = async () => {
	try {
		// TODO: Implement actual delete logic
		console.log('Deleting item:', item.value.id);
		
		// Simulate API call
		await new Promise(resolve => setTimeout(resolve, 1000));
		
		// Navigate back to items
		router.push('/items/home');
	} catch (error) {
		console.error('Error deleting item:', error);
		// TODO: Show error toast
	}
};

const claimItem = async () => {
	try {
		// TODO: Implement actual claim logic
		console.log('Claiming item:', item.value.id);
		
		// Simulate API call
		await new Promise(resolve => setTimeout(resolve, 1000));
		
		// Update status
		item.value.status = 'claimed';
		// TODO: Show success toast
	} catch (error) {
		console.error('Error claiming item:', error);
		// TODO: Show error toast
	}
};

const openImageModal = () => {
	// Implement the logic to open the image modal
	console.log('Opening image modal');
};

onMounted(() => {
	loadItem();
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

.item-header {
	color: white;
	padding: 24px;
	border-radius: 16px;
	margin-bottom: 24px;
	box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
}

.item-header.status-found {
	background: linear-gradient(135deg, var(--ion-color-success), var(--ion-color-success-shade));
}

.item-header.status-lost {
	background: linear-gradient(135deg, var(--ion-color-warning), var(--ion-color-warning-shade));
}

.item-header.status-claimed {
	background: linear-gradient(135deg, var(--ion-color-medium), var(--ion-color-medium-shade));
}

.header-content {
	display: flex;
	align-items: flex-start;
	gap: 16px;
	flex: 1;
}

.item-icon {
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

.item-info {
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

.status-chip {
	--background: rgba(255, 255, 255, 0.2);
	--color: white;
	margin-top: 8px;
}

.quick-actions {
	display: flex;
	gap: 8px;
}

.image-section {
	background: white;
	border-radius: 12px;
	padding: 20px;
	margin-bottom: 16px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.image-container {
	display: flex;
	align-items: center;
	gap: 16px;
}

.item-image {
	max-width: 100%;
	max-height: 200px;
	border-radius: 8px;
	cursor: pointer;
}

.view-fullsize-btn {
	--border-radius: 12px;
	height: 48px;
	font-weight: 600;
}

.description-section,
.metadata-section,
.actions-section {
	background: white;
	border-radius: 12px;
	padding: 20px;
	margin-bottom: 16px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.description-section h3,
.metadata-section h3,
.actions-section h3 {
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

.claim-button {
	--border-radius: 12px;
	height: 48px;
	font-weight: 600;
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

/* Responsive Design */
@media (max-width: 768px) {
	.content-wrapper {
		padding: 16px;
	}
	
	.item-header {
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
	
	.item-info {
		flex-direction: row;
		flex-wrap: wrap;
		gap: 12px;
	}
	
	.metadata-grid {
		gap: 12px;
	}
}

@media (max-width: 480px) {
	.header-content {
		align-items: center;
		text-align: center;
	}
	
	.item-info {
		justify-content: center;
	}
}

@media (min-width: 768px) {
	.metadata-grid {
		grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
	}
}
</style>
