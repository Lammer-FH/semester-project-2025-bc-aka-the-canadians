<template>
	<template-page
		:leftFooterButton="leftFooterButton"
		:rightFooterButton="rightFooterButton"
		@leftFooterButtonClicked="handleBack"
		@rightFooterButtonClicked="handleEdit">
		<div class="details-container">
			<div v-if="isLoading" class="loading-container">
				<ion-spinner name="crescent" color="primary"></ion-spinner>
				<p>Lade Bericht...</p>
			</div>

			<div v-else-if="error && !item" class="empty-state">
				<ion-icon :icon="alertCircleOutline" class="empty-icon"></ion-icon>
				<h2>Fehler beim Laden</h2>
				<p>{{ error }}</p>
				<ion-button @click="loadItem">
					<ion-icon :icon="refreshOutline" slot="start"></ion-icon>
					Erneut versuchen
				</ion-button>
			</div>

			<div v-else-if="item" class="item-details">
				<div class="report-header">
					<div class="header-content">
						<div class="header-left">
							<ion-chip
								:color="getStatusColor(item.status)"
								class="status-chip">
								<ion-icon
									:icon="getStatusIcon(item.status)"
									class="chip-icon"></ion-icon>
								{{ getStatusText(item.status) }}
							</ion-chip>
							<div class="report-meta">
								<span class="report-id">Bericht #{{ item.id }}</span>
								<span class="report-date">{{
									formatDate(item.createdAt)
								}}</span>
							</div>
						</div>
						<div class="header-right">
							<div class="view-count">
								<ion-icon :icon="eyeOutline" class="view-icon"></ion-icon>
								<span>{{ viewCount }} Aufrufe</span>
							</div>
							<div class="days-since">
								<ion-icon :icon="timeOutline" class="time-icon"></ion-icon>
								<span>Vor {{ daysSinceReported }} Tagen</span>
							</div>
						</div>
					</div>
				</div>

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
						<div class="image-overlay">
							<ion-chip color="dark" class="image-chip">
								<ion-icon :icon="cameraOutline" class="chip-icon"></ion-icon>
								Zum Vergrößern tippen
							</ion-chip>
						</div>
					</div>
				</div>

				<div class="info-card main-info">
					<div class="card-header">
						<h1 class="item-title">{{ item.name }}</h1>
						<div class="title-actions">
							<ion-button fill="clear" size="small" @click="shareItem">
								<ion-icon :icon="shareOutline" slot="icon-only"></ion-icon>
							</ion-button>
						</div>
					</div>

					<div class="status-section">
						<div
							v-if="item.status.toUpperCase() === 'FOUND'"
							class="found-item-section">
							<div class="action-banner found-banner">
								<div class="banner-content">
									<ion-icon :icon="eyeOutline" class="banner-icon"></ion-icon>
									<div class="banner-text">
										<h3>Gefundener Gegenstand</h3>
										<p>
											Ist das dein verlorener Gegenstand? Du kannst ihn zur
											Abholung anfordern.
										</p>
									</div>
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
						</div>

						<div
							v-else-if="item.status.toUpperCase() === 'LOST'"
							class="lost-item-section">
							<div class="action-banner lost-banner">
								<div class="banner-content">
									<ion-icon
										:icon="searchOutline"
										class="banner-icon"></ion-icon>
									<div class="banner-text">
										<h3>Verlorener Gegenstand</h3>
										<p>
											Jemand sucht nach diesem Gegenstand. Hast du ihn gefunden?
										</p>
									</div>
								</div>
								<ion-button
									expand="block"
									fill="outline"
									color="warning"
									@click="reportFound">
									<ion-icon :icon="megaphoneOutline" slot="start"></ion-icon>
									Fund melden
								</ion-button>
							</div>
						</div>

						<div
							v-else-if="item.status.toUpperCase() === 'CLAIMED'"
							class="claimed-item-section">
							<div class="action-banner claimed-banner">
								<div class="banner-content">
									<ion-icon
										:icon="checkmarkCircleOutline"
										class="banner-icon"></ion-icon>
									<div class="banner-text">
										<h3>Zur Abholung angefordert</h3>
										<p>
											Dieser Gegenstand wurde bereits zur Abholung angefordert.
										</p>
									</div>
								</div>
							</div>
						</div>

						<div
							v-else-if="item.status.toUpperCase() === 'RETURNED'"
							class="returned-item-section">
							<div class="action-banner returned-banner">
								<div class="banner-content">
									<ion-icon
										:icon="checkmarkCircleOutline"
										class="banner-icon"></ion-icon>
									<div class="banner-text">
										<h3>Erfolgreich zurückgegeben</h3>
										<p>
											Dieser Gegenstand wurde erfolgreich an den Besitzer
											zurückgegeben.
										</p>
									</div>
								</div>
							</div>
						</div>
					</div>

					<div v-if="item.description" class="description-section">
						<h3>
							<ion-icon
								:icon="documentTextOutline"
								class="section-icon"></ion-icon>
							Beschreibung
						</h3>
						<div class="description-content">
							<p class="description-text">
								{{ getCleanDescription(item.description) }}
							</p>

							<div
								v-if="getReporterInfo(item.description)"
								class="reporter-info">
								<h4>
									<ion-icon :icon="personOutline" class="info-icon"></ion-icon>
									Bericht-Details
								</h4>
								<div class="reporter-details">
									<div
										v-for="(info, key) in getReporterInfo(item.description)"
										:key="key"
										class="info-row">
										<span class="info-label">{{ key }}:</span>
										<span class="info-value">{{ info }}</span>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<div class="info-card location-info">
					<h3>
						<ion-icon :icon="locationOutline" class="section-icon"></ion-icon>
						Standort
					</h3>
					<div class="location-details">
						<div class="location-primary">
							<ion-icon
								:icon="businessOutline"
								class="location-icon"></ion-icon>
							<span class="location-name">{{ item.location.name }}</span>
						</div>
						<ion-button fill="clear" size="small" @click="viewLocationReports">
							<ion-icon :icon="flagOutline" slot="start"></ion-icon>
							Weitere Berichte an diesem Standort
						</ion-button>
					</div>
				</div>

				<div class="info-card timeline-info">
					<h3>
						<ion-icon :icon="timeOutline" class="section-icon"></ion-icon>
						Bericht-Verlauf
					</h3>
					<div class="timeline">
						<div class="timeline-item">
							<div class="timeline-marker created"></div>
							<div class="timeline-content">
								<h4>Bericht erstellt</h4>
								<p>{{ formatDetailedDate(item.createdAt) }}</p>
								<span class="timeline-type">{{
									getReportType(item.status)
								}}</span>
							</div>
						</div>
						<div v-if="item.updatedAt !== item.createdAt" class="timeline-item">
							<div class="timeline-marker updated"></div>
							<div class="timeline-content">
								<h4>Status aktualisiert</h4>
								<p>{{ formatDetailedDate(item.updatedAt) }}</p>
								<span class="timeline-status">{{
									getStatusText(item.status)
								}}</span>
							</div>
						</div>
						<div
							v-if="item.status.toUpperCase() === 'CLAIMED'"
							class="timeline-item">
							<div class="timeline-marker claimed"></div>
							<div class="timeline-content">
								<h4>Abholung angefordert</h4>
								<p>Warten auf Bestätigung</p>
							</div>
						</div>
					</div>
				</div>

				<div v-if="showStatistics" class="info-card stats-info">
					<h3>
						<ion-icon :icon="statsChartOutline" class="section-icon"></ion-icon>
						Statistiken
					</h3>
					<div class="stats-grid">
						<div class="stat-item">
							<div class="stat-number">{{ viewCount }}</div>
							<div class="stat-label">Aufrufe</div>
						</div>
						<div class="stat-item">
							<div class="stat-number">{{ daysSinceReported }}</div>
							<div class="stat-label">Tage alt</div>
						</div>
						<div class="stat-item">
							<div class="stat-number">{{ getLocationReportsCount() }}</div>
							<div class="stat-label">Berichte am Standort</div>
						</div>
					</div>
				</div>

				<div v-if="relatedReports.length > 0" class="info-card related-reports">
					<h3>
						<ion-icon :icon="layersOutline" class="section-icon"></ion-icon>
						Ähnliche Berichte
					</h3>
					<div class="related-list">
						<div
							v-for="related in relatedReports"
							:key="related.id"
							class="related-item"
							@click="navigateToReport(related.id)">
							<div class="related-content">
								<ion-chip
									:color="getStatusColor(related.status)"
									class="related-status">
									<ion-icon
										:icon="getStatusIcon(related.status)"
										class="chip-icon"></ion-icon>
									{{ getStatusText(related.status) }}
								</ion-chip>
								<h4>{{ related.name }}</h4>
								<p>
									{{ related.location }} • {{ getTimeAgo(related.createdAt) }}
								</p>
							</div>
							<ion-icon
								:icon="chevronForwardOutline"
								class="chevron-icon"></ion-icon>
						</div>
					</div>
				</div>

				<div class="action-buttons">
					<ion-button
						v-if="item.status.toUpperCase() === 'FOUND'"
						expand="block"
						size="large"
						color="success"
						@click="showClaimDialog">
						<ion-icon :icon="handRightOutline" slot="start"></ion-icon>
						Gegenstand abholen
					</ion-button>

					<ion-button
						v-else-if="item.status.toUpperCase() === 'LOST'"
						expand="block"
						size="large"
						fill="outline"
						color="warning"
						@click="reportFound">
						<ion-icon :icon="megaphoneOutline" slot="start"></ion-icon>
						Fund melden
					</ion-button>

					<div class="secondary-actions">
						<ion-button expand="block" fill="outline" @click="shareItem">
							<ion-icon :icon="shareOutline" slot="start"></ion-icon>
							Bericht teilen
						</ion-button>

						<ion-button
							expand="block"
							fill="outline"
							color="medium"
							@click="toggleStatistics">
							<ion-icon :icon="statsChartOutline" slot="start"></ion-icon>
							{{
								showStatistics
									? 'Statistiken ausblenden'
									: 'Statistiken anzeigen'
							}}
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
			</div>
		</div>

		<ion-modal :is-open="showImageModal" @did-dismiss="closeImageModal">
			<ion-header>
				<ion-toolbar>
					<ion-title>Bericht-Foto</ion-title>
					<ion-buttons slot="end">
						<ion-button @click="closeImageModal">
							<ion-icon :icon="closeOutline"></ion-icon>
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

		<ion-alert
			:is-open="showClaimAlert"
			header="Gegenstand abholen"
			:message="claimAlertMessage"
			:buttons="claimAlertButtons"
			@didDismiss="showClaimAlert = false"></ion-alert>

		<ion-alert
			:is-open="showDeleteAlert"
			header="Bericht löschen"
			message="Möchtest du diesen Bericht wirklich löschen? Diese Aktion kann nicht rückgängig gemacht werden."
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
	timeOutline,
	shareOutline,
	trashOutline,
	statsChartOutline,
	closeOutline,
	eyeOutline,
	flagOutline,
	checkmarkOutline,
	handRightOutline,
	searchOutline,
	megaphoneOutline,
	checkmarkCircleOutline,
	personOutline,
	cameraOutline,
	layersOutline,
	chevronForwardOutline,
} from 'ionicons/icons';
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useItemStore } from '@/stores/itemStore';
import type { Item } from '@/models/item';

const router = useRouter();
const route = useRoute();
const itemStore = useItemStore();

const item = ref<Item | null>(null);
const relatedReports = ref<Item[]>([]);
const showImageModal = ref(false);
const showDeleteAlert = ref(false);
const showClaimAlert = ref(false);
const showStatistics = ref(false);
const viewCount = ref(0);

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

const loadItem = async () => {
	try {
		const itemId = parseInt(route.params.id as string);
		if (isNaN(itemId)) {
			throw new Error('Invalid item ID');
		}

		const fetchedItem = await itemStore.fetchItemById(itemId);
		if (fetchedItem) {
			item.value = fetchedItem;
			await loadRelatedReports();
			await loadItemStatistics(itemId);
		}
	} catch (error) {
		console.error('Error loading item:', error);
	}
};

const loadRelatedReports = async () => {
	if (!item.value) return;

	try {
		await itemStore.fetchItems();
		const allItems = itemStore.getItems;

		relatedReports.value = allItems
			.filter(
				(i) =>
					i.id !== item.value!.id &&
					i.location === item.value!.location &&
					i.status.toUpperCase() !== 'CLAIMED'
			)
			.slice(0, 3);
	} catch (error) {
		console.error('Error loading related reports:', error);
		relatedReports.value = [];
	}
};

const loadItemStatistics = async (itemId: number) => {
	try {
		viewCount.value = Math.floor(Math.random() * 50) + 5;
	} catch (error) {
		console.error('Error loading statistics:', error);
	}
};

const getStatusColor = (status: string): string => {
	switch (status.toUpperCase()) {
		case 'FOUND':
			return 'success';
		case 'LOST':
			return 'warning';
		case 'CLAIMED':
			return 'medium';
		case 'RETURNED':
			return 'success';
		default:
			return 'primary';
	}
};

const getStatusIcon = (status: string): string => {
	switch (status.toUpperCase()) {
		case 'FOUND':
			return eyeOutline;
		case 'LOST':
			return searchOutline;
		case 'CLAIMED':
			return checkmarkOutline;
		case 'RETURNED':
			return checkmarkCircleOutline;
		default:
			return flagOutline;
	}
};

const getStatusText = (status: string): string => {
	switch (status.toUpperCase()) {
		case 'FOUND':
			return 'Gefunden';
		case 'LOST':
			return 'Verloren';
		case 'CLAIMED':
			return 'Abgeholt';
		case 'RETURNED':
			return 'Zurückgegeben';
		default:
			return status;
	}
};

const getReportType = (status: string): string => {
	return status.toUpperCase() === 'LOST' ? 'Verlustbericht' : 'Fundbericht';
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

const formatDetailedDate = (dateString: string) => {
	return new Date(dateString).toLocaleDateString('de-DE', {
		weekday: 'long',
		day: '2-digit',
		month: 'long',
		year: 'numeric',
		hour: '2-digit',
		minute: '2-digit',
	});
};

const getTimeAgo = (dateString: string) => {
	const date = new Date(dateString);
	const now = new Date();
	const diffInHours = Math.floor(
		(now.getTime() - date.getTime()) / (1000 * 60 * 60)
	);

	if (diffInHours < 1) return 'Vor wenigen Minuten';
	if (diffInHours < 24) return `Vor ${diffInHours} Stunden`;

	const diffInDays = Math.floor(diffInHours / 24);
	if (diffInDays < 7) return `Vor ${diffInDays} Tagen`;

	return date.toLocaleDateString('de-DE');
};

const getCleanDescription = (description: string): string => {
	const parts = description.split('--- Berichtinformationen ---');
	return parts[0].trim() || 'Keine Beschreibung verfügbar.';
};

const getReporterInfo = (
	description: string
): Record<string, string> | null => {
	const metadataMatch = description.match(
		/--- Berichtinformationen ---([\s\S]*?)(?:--- |$)/
	);
	if (!metadataMatch) return null;

	const metadata = metadataMatch[1];
	const info: Record<string, string> = {};

	const lines = metadata.split('\n').filter((line) => line.trim());
	lines.forEach((line) => {
		const [key, ...valueParts] = line.split(':');
		if (key && valueParts.length > 0) {
			info[key.trim()] = valueParts.join(':').trim();
		}
	});

	return Object.keys(info).length > 0 ? info : null;
};

const getLocationReportsCount = (): number => {
	return relatedReports.value.length + 1; // +1 for current item
};

const toggleStatistics = () => {
	showStatistics.value = !showStatistics.value;
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
		const success = await itemStore.deleteItem(item.value.id);
		if (success) {
			router.push('/items/overview');
		}
	} catch (error) {
		console.error('Error deleting item:', error);
	}
};

const showClaimDialog = () => {
	showClaimAlert.value = true;
};

const processClaim = async () => {
	if (!item.value) return;

	try {
		const currentDate = new Date().toLocaleDateString('de-DE');
		const claimDescription = [
			'--- ABHOLUNG ANGEFORDERT ---',
			`Angefordert am: ${currentDate}`,
			'Status: Warten auf Bestätigung des Finders',
			'',
			'--- ORIGINAL BESCHREIBUNG ---',
			item.value.description,
		].join('\n');

		const updatedItem = await itemStore.updateItem(item.value.id, {
			status: 'CLAIMED',
			description: claimDescription,
		});

		if (updatedItem) {
			item.value = updatedItem;
		}

		console.log('Item claimed successfully');
	} catch (error) {
		console.error('Error claiming item:', error);
	}
};

const reportFound = () => {
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
		const shareData = {
			title: `Lost & Found: ${item.value.name}`,
			text: `${getStatusText(item.value.status)}: ${item.value.name} am ${
				item.value.location
			}`,
			url: window.location.href,
		};

		if (navigator.share) {
			await navigator.share(shareData);
		} else {
			await navigator.clipboard.writeText(window.location.href);
			console.log('Link copied to clipboard');
		}
	} catch (error) {
		console.error('Error sharing:', error);
	}
};

const viewLocationReports = () => {
	router.push({
		path: '/items/overview',
		query: { location: item.value?.location },
	});
};

const navigateToReport = (reportId: number) => {
	router.push(`/items/${reportId}`);
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

.item-details {
	animation: slideInUp 0.6s ease-out;
}

.report-header {
	background: linear-gradient(
		135deg,
		var(--ion-color-primary),
		var(--ion-color-primary-shade)
	);
	color: white;
	padding: 20px;
	border-radius: 12px;
	margin-bottom: 16px;
	box-shadow: 0 4px 12px rgba(var(--ion-color-primary-rgb), 0.3);
}

.header-content {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	gap: 16px;
}

.header-left {
	flex: 1;
}

.status-chip {
	--background: rgba(255, 255, 255, 0.2);
	--color: white;
	margin-bottom: 8px;
}

.chip-icon {
	font-size: 14px;
	margin-right: 4px;
}

.report-meta {
	display: flex;
	flex-direction: column;
	gap: 4px;
	font-size: 0.9em;
	opacity: 0.9;
}

.report-id {
	font-weight: 600;
}

.report-date {
	opacity: 0.8;
}

.header-right {
	display: flex;
	flex-direction: column;
	gap: 8px;
	text-align: right;
	font-size: 0.85em;
	opacity: 0.9;
}

.view-count,
.days-since {
	display: flex;
	align-items: center;
	gap: 4px;
	justify-content: flex-end;
}

.view-icon,
.time-icon {
	font-size: 14px;
}

.image-section {
	margin-bottom: 16px;
}

.image-container {
	position: relative;
	border-radius: 12px;
	overflow: hidden;
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.item-image {
	width: 100%;
	height: 300px;
	object-fit: cover;
	transition: transform 0.3s ease;
	cursor: pointer;
}

.item-image:hover {
	transform: scale(1.02);
}

.expand-button {
	position: absolute;
	top: 12px;
	right: 12px;
	--background: rgba(0, 0, 0, 0.5);
	--color: white;
	--border-radius: 50%;
	backdrop-filter: blur(10px);
}

.image-overlay {
	position: absolute;
	bottom: 12px;
	left: 12px;
}

.image-chip {
	--background: rgba(0, 0, 0, 0.7);
	--color: white;
	backdrop-filter: blur(10px);
}

.info-card {
	background: white;
	border-radius: 12px;
	padding: 20px;
	margin-bottom: 16px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
	border: 1px solid var(--ion-color-light-shade);
}

.card-header {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	margin-bottom: 16px;
}

.item-title {
	color: var(--ion-color-dark);
	margin: 0;
	font-size: 1.8em;
	font-weight: 600;
	line-height: 1.2;
}

.title-actions {
	display: flex;
	gap: 8px;
}

.status-section {
	margin-bottom: 20px;
}

.action-banner {
	border-radius: 12px;
	padding: 20px;
	border-left: 4px solid;
}

.found-banner {
	background: linear-gradient(
		135deg,
		var(--ion-color-success-tint),
		rgba(var(--ion-color-success-rgb), 0.1)
	);
	border-left-color: var(--ion-color-success);
}

.lost-banner {
	background: linear-gradient(
		135deg,
		var(--ion-color-warning-tint),
		rgba(var(--ion-color-warning-rgb), 0.1)
	);
	border-left-color: var(--ion-color-warning);
}

.claimed-banner,
.returned-banner {
	background: linear-gradient(
		135deg,
		var(--ion-color-medium-tint),
		rgba(var(--ion-color-medium-rgb), 0.1)
	);
	border-left-color: var(--ion-color-medium);
}

.banner-content {
	display: flex;
	align-items: flex-start;
	gap: 16px;
	margin-bottom: 16px;
}

.banner-icon {
	font-size: 24px;
	margin-top: 4px;
	flex-shrink: 0;
}

.banner-text h3 {
	color: var(--ion-color-dark);
	margin: 0 0 4px 0;
	font-weight: 600;
}

.banner-text p {
	color: var(--ion-color-medium-shade);
	margin: 0;
	line-height: 1.4;
}

.claim-button {
	--background: var(--ion-color-success);
	--color: white;
	font-weight: 600;
	font-size: 1.1em;
	animation: pulse 2s infinite;
}

@keyframes pulse {
	0% {
		transform: scale(1);
	}
	50% {
		transform: scale(1.02);
	}
	100% {
		transform: scale(1);
	}
}

.description-section h3 {
	color: var(--ion-color-dark);
	margin: 0 0 16px 0;
	font-size: 1.1em;
	font-weight: 600;
	display: flex;
	align-items: center;
	gap: 8px;
}

.section-icon {
	font-size: 18px;
	color: var(--ion-color-primary);
}

.description-content {
	display: flex;
	flex-direction: column;
	gap: 16px;
}

.description-text {
	color: var(--ion-color-medium-shade);
	line-height: 1.6;
	margin: 0;
	font-size: 1em;
}

.reporter-info {
	background: var(--ion-color-light-tint);
	border-radius: 8px;
	padding: 16px;
	border-left: 4px solid var(--ion-color-primary);
}

.reporter-info h4 {
	color: var(--ion-color-dark);
	margin: 0 0 12px 0;
	font-size: 1em;
	font-weight: 600;
	display: flex;
	align-items: center;
	gap: 8px;
}

.info-icon {
	font-size: 16px;
	color: var(--ion-color-primary);
}

.reporter-details {
	display: flex;
	flex-direction: column;
	gap: 8px;
}

.info-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
	font-size: 0.9em;
}

.info-label {
	color: var(--ion-color-medium);
	font-weight: 500;
}

.info-value {
	color: var(--ion-color-dark);
	text-align: right;
}

.location-details {
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.location-primary {
	display: flex;
	align-items: center;
	gap: 12px;
	font-size: 1.1em;
}

.location-icon {
	font-size: 20px;
	color: var(--ion-color-primary);
}

.location-name {
	color: var(--ion-color-dark);
	font-weight: 500;
}

.timeline {
	display: flex;
	flex-direction: column;
	gap: 16px;
}

.timeline-item {
	display: flex;
	align-items: flex-start;
	gap: 16px;
	position: relative;
}

.timeline-item:not(:last-child)::after {
	content: '';
	position: absolute;
	left: 11px;
	top: 24px;
	bottom: -16px;
	width: 2px;
	background: var(--ion-color-light-shade);
}

.timeline-marker {
	width: 24px;
	height: 24px;
	border-radius: 50%;
	border: 3px solid;
	background: white;
	flex-shrink: 0;
	margin-top: 2px;
}

.timeline-marker.created {
	border-color: var(--ion-color-primary);
}

.timeline-marker.updated {
	border-color: var(--ion-color-warning);
}

.timeline-marker.claimed {
	border-color: var(--ion-color-success);
}

.timeline-content h4 {
	color: var(--ion-color-dark);
	margin: 0 0 4px 0;
	font-weight: 600;
	font-size: 0.95em;
}

.timeline-content p {
	color: var(--ion-color-medium);
	margin: 0 0 4px 0;
	font-size: 0.9em;
}

.timeline-type,
.timeline-status {
	display: inline-block;
	padding: 2px 8px;
	border-radius: 12px;
	font-size: 0.75em;
	font-weight: 500;
	background: var(--ion-color-light-shade);
	color: var(--ion-color-medium-shade);
}

.stats-grid {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 16px;
}

.stat-item {
	text-align: center;
	background: var(--ion-color-light-tint);
	padding: 16px;
	border-radius: 8px;
}

.stat-number {
	font-size: 1.8em;
	font-weight: 700;
	color: var(--ion-color-primary);
	margin-bottom: 4px;
}

.stat-label {
	color: var(--ion-color-medium);
	font-size: 0.85em;
	font-weight: 500;
}

.related-list {
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.related-item {
	display: flex;
	align-items: center;
	gap: 12px;
	padding: 12px;
	background: var(--ion-color-light-tint);
	border-radius: 8px;
	cursor: pointer;
	transition: all 0.2s ease;
}

.related-item:hover {
	background: var(--ion-color-light-shade);
	transform: translateX(4px);
}

.related-content {
	flex: 1;
}

.related-status {
	font-size: 0.8em;
	margin-bottom: 4px;
}

.related-content h4 {
	color: var(--ion-color-dark);
	margin: 0 0 4px 0;
	font-weight: 600;
	font-size: 0.95em;
}

.related-content p {
	color: var(--ion-color-medium);
	margin: 0;
	font-size: 0.85em;
}

.chevron-icon {
	color: var(--ion-color-medium);
	font-size: 16px;
}

.action-buttons {
	display: flex;
	flex-direction: column;
	gap: 12px;
	margin-top: 24px;
}

.secondary-actions {
	display: grid;
	grid-template-columns: 1fr;
	gap: 8px;
	margin-top: 8px;
}

.modal-content {
	background: black;
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
}

:global(.alert-button-confirm) {
	color: var(--ion-color-success) !important;
	font-weight: 600 !important;
}

:global(.alert-button-cancel) {
	color: var(--ion-color-medium) !important;
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

@media (max-width: 768px) {
	.details-container {
		padding: 12px;
	}

	.header-content {
		flex-direction: column;
		gap: 12px;
	}

	.header-right {
		align-self: stretch;
		text-align: left;
	}

	.view-count,
	.days-since {
		justify-content: flex-start;
	}

	.stats-grid {
		grid-template-columns: 1fr;
	}

	.secondary-actions {
		grid-template-columns: 1fr;
	}

	.banner-content {
		flex-direction: column;
		gap: 12px;
		text-align: center;
	}
}

@media (max-width: 480px) {
	.item-title {
		font-size: 1.5em;
	}

	.card-header {
		flex-direction: column;
		gap: 12px;
	}

	.title-actions {
		align-self: stretch;
		justify-content: center;
	}

	.info-row {
		flex-direction: column;
		align-items: flex-start;
		gap: 4px;
	}

	.info-value {
		text-align: left;
	}
}

@media (min-width: 768px) {
	.secondary-actions {
		grid-template-columns: repeat(3, 1fr);
	}
}
</style>
