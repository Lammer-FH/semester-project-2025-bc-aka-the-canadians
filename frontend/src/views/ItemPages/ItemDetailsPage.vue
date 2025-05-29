<template>
	<template-page
		:leftFooterButton="leftFooterButton"
		:rightFooterButton="rightFooterButton"
		@leftFooterButtonClicked="handleBack"
		@rightFooterButtonClicked="handleEdit">
		<div class="details-container">
			<!-- Loading State -->
			<div v-if="isLoading" class="loading-container">
				<ion-spinner name="crescent" color="primary"></ion-spinner>
				<p>Lade Gegenstand...</p>
			</div>

			<!-- Error State -->
			<div v-else-if="error && !item" class="empty-state">
				<ion-icon :icon="alertCircleOutline" class="empty-icon"></ion-icon>
				<h2>Fehler beim Laden</h2>
				<p>{{ error }}</p>
				<ion-button @click="loadItem">
					<ion-icon :icon="refreshOutline" slot="start"></ion-icon>
					Erneut versuchen
				</ion-button>
			</div>

			<!-- Item Details -->
			<div v-else-if="item" class="item-details">
				<!-- Image Section -->
				<div v-if="item.imageUrl" class="image-section">
					<div class="image-container">
						<img
							:src="item.imageUrl"
							:alt="item.name"
							class="item-image"
							@click="openImageModal" />
						<ion-button
							fill="clear"
							class="expand-button"
							@click="openImageModal">
							<ion-icon :icon="expandOutline" slot="icon-only"></ion-icon>
						</ion-button>
					</div>
				</div>

				<!-- Enhanced Status Section -->
				<div class="info-card status-card">
					<div class="card-header">
						<div class="title-section">
							<h1 class="item-title">{{ item.name }}</h1>
							<ion-chip
								:color="getStatusColor(item.status)"
								class="status-chip">
								<ion-icon
									:icon="getStatusIcon(item.status)"
									class="chip-icon"></ion-icon>
								{{ getStatusText(item.status) }}
							</ion-chip>
						</div>
					</div>

					<!-- Claim Action Section for Found Items -->
					<div
						v-if="item.status.toUpperCase() === 'FOUND'"
						class="claim-action-section">
						<div class="claim-info">
							<h3>
								<ion-icon
									:icon="handRightOutline"
									class="section-icon"></ion-icon>
								Ist das dein verlorener Gegenstand?
							</h3>
							<p>
								Wenn du glaubst, dass dies dein verlorener Gegenstand ist,
								kannst du ihn hier zur Abholung anfordern.
							</p>
						</div>
						<ion-button
							expand="block"
							size="large"
							color="success"
							class="claim-button"
							@click="showClaimDialog">
							<ion-icon :icon="handRightOutline" slot="start"></ion-icon>
							Gegenstand abholen
						</ion-button>
					</div>

					<!-- Already Claimed Section -->
					<div
						v-else-if="item.status.toUpperCase() === 'CLAIMED'"
						class="claimed-section">
						<div class="claimed-info">
							<ion-icon
								:icon="checkmarkCircleOutline"
								class="claimed-icon"></ion-icon>
							<div class="claimed-text">
								<h3>Bereits zur Abholung angefordert</h3>
								<p>
									Dieser Gegenstand wurde bereits von jemandem zur Abholung
									angefordert.
								</p>
							</div>
						</div>
					</div>

					<!-- Lost Item Section -->
					<div
						v-else-if="item.status.toUpperCase() === 'LOST'"
						class="lost-section">
						<div class="lost-info">
							<ion-icon :icon="searchOutline" class="lost-icon"></ion-icon>
							<div class="lost-text">
								<h3>Verlorener Gegenstand</h3>
								<p>
									Wenn du diesen Gegenstand gefunden hast, kontaktiere bitte den
									Besitzer oder melde einen Fundbericht.
								</p>
							</div>
						</div>
						<ion-button expand="block" fill="outline" @click="reportFound">
							<ion-icon :icon="megaphoneOutline" slot="start"></ion-icon>
							Fund melden
						</ion-button>
					</div>
				</div>

				<!-- Main Info Card -->
				<div class="info-card main-info">
					<div class="card-header">
						<div class="title-section">
							<h1 class="item-title">{{ item.name }}</h1>
							<ion-chip
								:color="getStatusColor(item.status)"
								class="status-chip">
								<ion-icon
									:icon="getStatusIcon(item.status)"
									class="chip-icon"></ion-icon>
								{{ getStatusText(item.status) }}
							</ion-chip>
						</div>
					</div>

					<div v-if="item.description" class="description-section">
						<h3>
							<ion-icon
								:icon="documentTextOutline"
								class="section-icon"></ion-icon>
							Beschreibung
						</h3>
						<p class="description-text">{{ item.description }}</p>
					</div>
				</div>

				<!-- Location Info Card -->
				<div class="info-card location-info">
					<h3>
						<ion-icon :icon="locationOutline" class="section-icon"></ion-icon>
						Standort
					</h3>
					<div class="location-details">
						<ion-item class="detail-item">
							<ion-icon
								:icon="businessOutline"
								slot="start"
								color="primary"></ion-icon>
							<ion-label>
								<h4>Fundort/Verlustort</h4>
								<p>{{ item.location }}</p>
							</ion-label>
						</ion-item>
					</div>
				</div>

				<!-- Metadata Card -->
				<div class="info-card metadata-info">
					<h3>
						<ion-icon
							:icon="informationCircleOutline"
							class="section-icon"></ion-icon>
						Details
					</h3>
					<div class="metadata-grid">
						<div class="metadata-item">
							<ion-icon
								:icon="calendarOutline"
								class="metadata-icon"></ion-icon>
							<div class="metadata-content">
								<span class="metadata-label">Gemeldet am</span>
								<span class="metadata-value">{{
									formatDate(item.createdAt)
								}}</span>
							</div>
						</div>
						<div v-if="item.updatedAt !== item.createdAt" class="metadata-item">
							<ion-icon :icon="timeOutline" class="metadata-icon"></ion-icon>
							<div class="metadata-content">
								<span class="metadata-label">Zuletzt aktualisiert</span>
								<span class="metadata-value">{{
									formatDate(item.updatedAt)
								}}</span>
							</div>
						</div>
						<div class="metadata-item">
							<ion-icon
								:icon="fingerPrintOutline"
								class="metadata-icon"></ion-icon>
							<div class="metadata-content">
								<span class="metadata-label">ID</span>
								<span class="metadata-value">#{{ item.id }}</span>
							</div>
						</div>
					</div>
				</div>

				<!-- Actions Card -->
				<div class="info-card actions-card">
					<h3>
						<ion-icon :icon="settingsOutline" class="section-icon"></ion-icon>
						Aktionen
					</h3>
					<div class="action-buttons">
						<ion-button expand="block" fill="outline" @click="shareItem">
							<ion-icon :icon="shareOutline" slot="start"></ion-icon>
							Teilen
						</ion-button>

						<ion-button
							v-if="item.status.toUpperCase() === 'FOUND'"
							expand="block"
							color="success"
							@click="showClaimDialog">
							<ion-icon :icon="handRightOutline" slot="start"></ion-icon>
							Gegenstand abholen
						</ion-button>

						<ion-button
							expand="block"
							fill="outline"
							color="danger"
							@click="showDeleteConfirmation">
							<ion-icon :icon="trashOutline" slot="start"></ion-icon>
							Bericht löschen
						</ion-button>
					</div>
				</div>

				<!-- Statistics Card (if available) -->
				<div v-if="showStatistics" class="info-card stats-card">
					<h3>
						<ion-icon :icon="statsChartOutline" class="section-icon"></ion-icon>
						Statistiken
					</h3>
					<div class="stats-grid">
						<div class="stat-item">
							<span class="stat-number">{{ viewCount }}</span>
							<span class="stat-label">Aufrufe</span>
						</div>
						<div class="stat-item">
							<span class="stat-number">{{ daysSinceReported }}</span>
							<span class="stat-label">Tage online</span>
						</div>
					</div>
				</div>
			</div>
		</div>

		<!-- Enhanced Claim Alert -->
		<ion-alert
			:is-open="showClaimAlert"
			header="Gegenstand abholen"
			:message="claimAlertMessage"
			:buttons="claimAlertButtons"
			@didDismiss="showClaimAlert = false">
		</ion-alert>

		<!-- Image Modal -->
		<ion-modal :is-open="showImageModal" @didDismiss="closeImageModal">
			<ion-header>
				<ion-toolbar>
					<ion-title>{{ item?.name }}</ion-title>
					<ion-buttons slot="end">
						<ion-button @click="closeImageModal">
							<ion-icon :icon="closeOutline" slot="icon-only"></ion-icon>
						</ion-button>
					</ion-buttons>
				</ion-toolbar>
			</ion-header>
			<ion-content class="modal-content">
				<div class="modal-image-container">
					<img
						v-if="item?.imageUrl"
						:src="item.imageUrl"
						:alt="item.name"
						class="modal-image" />
				</div>
			</ion-content>
		</ion-modal>

		<!-- Delete Confirmation Alert -->
		<ion-alert
			:is-open="showDeleteAlert"
			header="Gegenstand löschen"
			message="Bist du sicher, dass du diesen Gegenstand löschen möchtest? Diese Aktion kann nicht rückgängig gemacht werden."
			:buttons="deleteAlertButtons"
			@didDismiss="showDeleteAlert = false"></ion-alert>
	</template-page>
</template>

<script setup lang="ts">
import TemplatePage from '@/components/TemplatePage.vue';
import {
	IonIcon,
	IonButton,
	IonSpinner,
	IonChip,
	IonItem,
	IonLabel,
	IonModal,
	IonHeader,
	IonToolbar,
	IonTitle,
	IonButtons,
	IonContent,
	IonAlert,
} from '@ionic/vue';
import {
	arrowBackOutline,
	createOutline,
	alertCircleOutline,
	refreshOutline,
	expandOutline,
	documentTextOutline,
	locationOutline,
	businessOutline,
	informationCircleOutline,
	calendarOutline,
	timeOutline,
	fingerPrintOutline,
	settingsOutline,
	checkmarkCircleOutline,
	shareOutline,
	trashOutline,
	statsChartOutline,
	closeOutline,
	eyeOutline,
	eyeOffOutline,
	flagOutline,
	checkmarkOutline,
	handRightOutline,
	searchOutline,
	megaphoneOutline,
} from 'ionicons/icons';
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useItemStore } from '@/stores/itemStore';
import type { Item } from '@/models/item';

const router = useRouter();
const route = useRoute();
const itemStore = useItemStore();

// State
const item = ref<Item | null>(null);
const showImageModal = ref(false);
const showDeleteAlert = ref(false);
const showClaimAlert = ref(false);
const viewCount = ref(0);
const showStatistics = ref(false);

// Computed
const isLoading = computed(() => itemStore.isLoading);
const error = computed(() => itemStore.getError);

const daysSinceReported = computed(() => {
	if (!item.value?.createdAt) return 0;
	const reportDate = new Date(item.value.createdAt);
	const now = new Date();
	const diffTime = Math.abs(now.getTime() - reportDate.getTime());
	return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
});

const leftFooterButton = computed(() => ({
	name: 'Zurück',
	color: 'medium',
	icon: arrowBackOutline,
}));

const rightFooterButton = computed(() => ({
	name: 'Bearbeiten',
	color: 'primary',
	icon: createOutline,
}));

const deleteAlertButtons = [
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

const claimAlertMessage = computed(() => {
	if (!item.value) return '';

	return `
        <div style="text-align: left; padding: 8px;">
            <p><strong>Gegenstand:</strong> ${item.value.name}</p>
            <p><strong>Standort:</strong> ${item.value.location}</p>
            <br>
            <p>Möchtest du diesen Gegenstand zur Abholung anfordern?</p>
            <p style="color: #666; font-size: 0.9em;">
                Der Finder wird benachrichtigt und kann dich kontaktieren, 
                um die Abholung zu koordinieren.
            </p>
        </div>
    `;
});

const claimAlertButtons = [
	{
		text: 'Abbrechen',
		role: 'cancel',
		cssClass: 'alert-button-cancel',
	},
	{
		text: 'Ja, abholen',
		cssClass: 'alert-button-confirm',
		handler: () => processClaim(),
	},
];

// Methods
const loadItem = async () => {
	try {
		const itemId = Number(route.params.id);
		if (!itemId) {
			throw new Error('Invalid item ID');
		}

		const loadedItem = await itemStore.fetchItemById(itemId);
		if (loadedItem) {
			item.value = loadedItem;
			// TODO: Load view statistics from backend
			loadItemStatistics(itemId);
		} else {
			throw new Error('Item not found');
		}
	} catch (error) {
		console.error('Error loading item:', error);
		// Navigate back if item cannot be loaded
		router.back();
	}
};

const loadItemStatistics = async (itemId: number) => {
	try {
		// TODO: Implement API call to get item statistics
		// For now, using mock data
		viewCount.value = Math.floor(Math.random() * 50) + 1;
		showStatistics.value = true;
	} catch (error) {
		console.error('Error loading item statistics:', error);
		showStatistics.value = false;
	}
};

const getStatusColor = (status: string): string => {
	switch (status.toUpperCase()) {
		case 'LOST':
			return 'danger';
		case 'FOUND':
			return 'warning';
		case 'CLAIMED':
			return 'success';
		default:
			return 'medium';
	}
};

const getStatusIcon = (status: string): string => {
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

const getStatusText = (status: string): string => {
	switch (status.toUpperCase()) {
		case 'LOST':
			return 'Verloren';
		case 'FOUND':
			return 'Gefunden';
		case 'CLAIMED':
			return 'Abgeholt';
		default:
			return status;
	}
};

const formatDate = (dateString: string) => {
	return new Date(dateString).toLocaleDateString('de-DE', {
		day: '2-digit',
		month: '2-digit',
		year: 'numeric',
		hour: '2-digit',
		minute: '2-digit',
	});
};

const handleBack = () => {
	router.back();
};

const handleEdit = () => {
	if (item.value) {
		router.push(`/items/${item.value.id}/edit`);
	}
};

const openImageModal = () => {
	showImageModal.value = true;
};

const closeImageModal = () => {
	showImageModal.value = false;
};

const showDeleteConfirmation = () => {
	showDeleteAlert.value = true;
};

const confirmDelete = async () => {
	if (!item.value) return;

	try {
		await itemStore.deleteItem(item.value.id);
		router.push('/items/overview');
	} catch (error) {
		console.error('Error deleting item:', error);
		// TODO: Show error toast
	}
};

const markAsClaimed = () => {
	showClaimAlert.value = true;
};

const showClaimDialog = () => {
	showClaimAlert.value = true;
};

const processClaim = async () => {
	if (!item.value) return;

	try {
		// Create enhanced description with claim information
		const currentDate = new Date().toLocaleDateString('de-DE');
		const claimDescription = [
			'--- ABHOLUNG ANGEFORDERT ---',
			`Angefordert am: ${currentDate}`,
			'Status: Warten auf Bestätigung des Finders',
			'',
			'--- ORIGINAL BESCHREIBUNG ---',
			item.value.description,
		].join('\n');

		// Update item status
		await itemStore.updateItem(item.value.id, {
			status: 'CLAIMED',
			description: claimDescription,
		});

		// Reload item data
		await loadItem();

		// TODO: Show success toast
		console.log('Item claimed successfully');

		// TODO: Send notification to finder
		// await notificationService.notifyItemClaimed(item.value.id);
	} catch (error) {
		console.error('Error claiming item:', error);
		// TODO: Show error toast
	}
};

const reportFound = () => {
	// Navigate to report creation with pre-filled data
	router.push({
		path: '/items/report',
		query: {
			type: 'FOUND',
			name: item.value?.name,
			location: item.value?.location,
		},
	});
};

const shareItem = async () => {
	if (!item.value) return;

	try {
		const shareUrl = `${window.location.origin}/items/${item.value.id}`;
		const shareData = {
			title: item.value.name,
			text: `${item.value.description || 'Gegenstand gefunden/verloren'}`,
			url: shareUrl,
		};

		if (navigator.share) {
			await navigator.share(shareData);
		} else {
			// Fallback: copy to clipboard
			await navigator.clipboard.writeText(shareUrl);
			// TODO: Show success toast
			console.log('Link copied to clipboard');
		}
	} catch (error) {
		console.error('Error sharing item:', error);
		// TODO: Show error toast
	}
};

onMounted(() => {
	loadItem();
});
</script>

<style scoped>
.details-container {
	padding: 16px;
	max-width: 800px;
	margin: 0 auto;
	min-height: 100vh;
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

.item-details {
	animation: slideInUp 0.6s ease-out;
}

.image-section {
	margin-bottom: 20px;
}

.image-container {
	position: relative;
	border-radius: 16px;
	overflow: hidden;
	box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.item-image {
	width: 100%;
	height: 300px;
	object-fit: cover;
	cursor: pointer;
	transition: transform 0.3s ease;
}

.item-image:hover {
	transform: scale(1.02);
}

.expand-button {
	position: absolute;
	top: 12px;
	right: 12px;
	background: rgba(255, 255, 255, 0.9);
	border-radius: 50%;
	--padding-start: 8px;
	--padding-end: 8px;
	--padding-top: 8px;
	--padding-bottom: 8px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.info-card {
	background: white;
	border-radius: 16px;
	padding: 20px;
	margin-bottom: 16px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
	border: 1px solid var(--ion-color-light-shade);
}

.card-header {
	margin-bottom: 20px;
}

.title-section {
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.item-title {
	color: var(--ion-color-dark);
	margin: 0;
	font-size: 1.5em;
	font-weight: 700;
	line-height: 1.2;
}

.status-chip {
	align-self: flex-start;
	font-weight: 600;
}

.chip-icon {
	font-size: 16px;
	margin-right: 4px;
}

.description-section h3,
.info-card h3 {
	display: flex;
	align-items: center;
	gap: 8px;
	color: var(--ion-color-dark);
	margin: 0 0 16px 0;
	font-size: 1.1em;
	font-weight: 600;
}

.section-icon {
	font-size: 20px;
	color: var(--ion-color-primary);
}

.description-text {
	color: var(--ion-color-dark);
	line-height: 1.6;
	margin: 0;
	font-size: 1em;
}

.location-details,
.action-buttons {
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.detail-item {
	--background: var(--ion-color-light-tint);
	--border-radius: 8px;
	--padding-start: 12px;
	--padding-end: 12px;
	border-radius: 8px;
}

.detail-item h4 {
	margin: 0 0 4px 0;
	font-weight: 600;
	color: var(--ion-color-dark);
}

.detail-item p {
	margin: 0;
	color: var(--ion-color-medium);
	font-size: 0.9em;
}

.metadata-grid {
	display: grid;
	grid-template-columns: 1fr;
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
	flex-shrink: 0;
}

.metadata-content {
	display: flex;
	flex-direction: column;
	gap: 2px;
}

.metadata-label {
	font-size: 0.8em;
	color: var(--ion-color-medium);
	font-weight: 500;
}

.metadata-value {
	font-size: 0.9em;
	color: var(--ion-color-dark);
	font-weight: 600;
}

.action-btn {
	--border-radius: 12px;
	height: 48px;
	font-weight: 600;
	margin-bottom: 8px;
}

.stats-grid {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
	gap: 16px;
}

.stat-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 16px;
	background: var(--ion-color-light-tint);
	border-radius: 12px;
	text-align: center;
}

.stat-number {
	font-size: 1.8em;
	font-weight: 700;
	color: var(--ion-color-primary);
	line-height: 1;
}

.stat-label {
	font-size: 0.8em;
	color: var(--ion-color-medium);
	margin-top: 4px;
	font-weight: 500;
}

/* Modal Styles */
.modal-content {
	--background: var(--ion-color-dark);
}

.modal-image-container {
	display: flex;
	align-items: center;
	justify-content: center;
	height: 100%;
	padding: 20px;
}

.modal-image {
	max-width: 100%;
	max-height: 100%;
	object-fit: contain;
	border-radius: 8px;
}

@keyframes slideInUp {
	from {
		opacity: 0;
		transform: translateY(30px);
	}
	to {
		opacity: 1;
		transform: translateY(0);
	}
}

/* Enhanced Status Section Styles */
.status-card {
	background: linear-gradient(135deg, var(--ion-color-light-tint), white);
	border: 2px solid var(--ion-color-light-shade);
	position: relative;
	overflow: hidden;
}

.claim-action-section {
	background: linear-gradient(
		135deg,
		var(--ion-color-success-tint),
		rgba(var(--ion-color-success-rgb), 0.1)
	);
	border-radius: 12px;
	padding: 20px;
	margin-top: 16px;
	border-left: 4px solid var(--ion-color-success);
}

.claim-info h3 {
	color: var(--ion-color-dark);
	margin: 0 0 8px 0;
	font-weight: 600;
	display: flex;
	align-items: center;
	gap: 8px;
}

.claim_info p {
	color: var(--ion-color-medium-shade);
	margin: 0 0 16px 0;
	line-height: 1.4;
}

.claim-button {
	--background: var(--ion-color-success);
	--color: white;
	font-weight: 600;
	font-size: 1.1em;
	animation: glow 2s ease-in-out infinite alternate;
}

@keyframes glow {
	from {
		box-shadow: 0 0 10px rgba(var(--ion-color-success-rgb), 0.5);
	}
	to {
		box-shadow: 0 0 20px rgba(var(--ion-color-success-rgb), 0.8),
			0 0 30px rgba(var(--ion-color-success-rgb), 0.6);
	}
}

.claimed-section {
	background: linear-gradient(
		135deg,
		var(--ion-color-medium-tint),
		rgba(var(--ion-color-medium-rgb), 0.1)
	);
	border-radius: 12px;
	padding: 20px;
	margin-top: 16px;
	border-left: 4px solid var(--ion-color-medium);
}

.claimed-info {
	display: flex;
	align-items: flex-start;
	gap: 16px;
}

.claimed-icon {
	font-size: 24px;
	color: var(--ion-color-medium);
	margin-top: 4px;
	flex-shrink: 0;
}

.claimed-text h3 {
	color: var(--ion-color-dark);
	margin: 0 0 4px 0;
	font-weight: 600;
}

.claimed-text p {
	color: var(--ion-color-medium-shade);
	margin: 0;
	line-height: 1.4;
}

.lost-section {
	background: linear-gradient(
		135deg,
		var(--ion-color-warning-tint),
		rgba(var(--ion-color-warning-rgb), 0.1)
	);
	border-radius: 12px;
	padding: 20px;
	margin-top: 16px;
	border-left: 4px solid var(--ion-color-warning);
}

.lost-info {
	display: flex;
	align-items: flex-start;
	gap: 16px;
	margin-bottom: 16px;
}

.lost-icon {
	font-size: 24px;
	color: var(--ion-color-warning);
	margin-top: 4px;
	flex-shrink: 0;
}

.lost-text h3 {
	color: var(--ion-color-dark);
	margin: 0 0 4px 0;
	font-weight: 600;
}

.lost-text p {
	color: var(--ion-color-medium-shade);
	margin: 0;
	line-height: 1.4;
}

.action-buttons {
	display: grid;
	grid-template-columns: 1fr;
	gap: 12px;
	margin-top: 24px;
}

/* Alert Styles */
:global(.alert-button-confirm) {
	color: var(--ion-color-success) !important;
	font-weight: 600 !important;
}

:global(.alert-button-cancel) {
	color: var(--ion-color-medium) !important;
}

/* Responsive Design */
@media (min-width: 768px) {
	.action-buttons {
		grid-template-columns: repeat(2, 1fr);
	}
}

@media (max-width: 480px) {
	.claim-action-section,
	.claimed-section,
	.lost-section {
		padding: 16px;
	}

	.claimed-info,
	.lost-info {
		flex-direction: column;
		gap: 12px;
	}

	.claimed-icon,
	.lost-icon {
		align-self: center;
	}
}
</style>
