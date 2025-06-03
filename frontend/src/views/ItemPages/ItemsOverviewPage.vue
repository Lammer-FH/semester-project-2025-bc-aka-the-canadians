<template>
	<template-page
		:headline="'Browse Reports'"
		addButtonPath="/items/report"
		addButtonText="Create New Report">
		<template #header>
			<NavigationTabs v-model="activeTab" />
		</template>

		<div class="reports-container">
			<div class="search-and-filter">
				<ion-searchbar
					v-model="searchTerm"
					placeholder="Search for reports..."
					debounce="300"
					class="custom-searchbar"></ion-searchbar>

				<ion-button
					fill="outline"
					size="small"
					class="filter-button"
					@click="toggleFilterModal">
					<ion-icon :icon="funnelOutline" slot="start"></ion-icon>
					Filter
					<ion-badge v-if="activeFiltersCount > 0" class="filter-badge">
						{{ activeFiltersCount }}
					</ion-badge>
				</ion-button>
			</div>

			<div v-if="activeFiltersCount > 0" class="filter-chips">
				<ion-chip
					v-if="selectedStatus"
					class="filter-chip"
					@click="clearStatusFilter">
					<ion-icon :icon="flagOutline"></ion-icon>
					<ion-label>{{ getStatusText(selectedStatus) }}</ion-label>
					<ion-icon :icon="closeOutline"></ion-icon>
				</ion-chip>
				<ion-chip
					v-if="selectedLocation"
					class="filter-chip"
					@click="clearLocationFilter">
					<ion-icon :icon="locationOutline"></ion-icon>
					<ion-label>{{ selectedLocation }}</ion-label>
					<ion-icon :icon="closeOutline"></ion-icon>
				</ion-chip>
				<ion-button
					fill="clear"
					size="small"
					class="clear-all-button"
					@click="clearAllFilters">
					Clear All Filters
				</ion-button>
			</div>

			<div class="stats-summary">
				<div class="stat-item">
					<ion-icon :icon="eyeOutline" color="success"></ion-icon>
					<span>{{ foundReportsCount }} Found Reports</span>
				</div>
				<div class="stat-item">
					<ion-icon :icon="searchOutline" color="warning"></ion-icon>
					<span>{{ lostReportsCount }} Lost Reports</span>
				</div>
				<div class="stat-item">
					<ion-icon :icon="checkmarkCircleOutline" color="medium"></ion-icon>
					<span>{{ claimedReportsCount }} Completed</span>
				</div>
			</div>

			<div v-if="isLoading" class="loading-container">
				<ion-spinner name="crescent" color="primary"></ion-spinner>
				<p>Loading reports...</p>
			</div>

			<div v-else-if="error" class="empty-state">
				<ion-icon :icon="alertCircleOutline" class="empty-icon"></ion-icon>
				<h2>Error Loading</h2>
				<p>{{ error }}</p>
				<ion-button @click="loadReports">
					<ion-icon :icon="refreshOutline" slot="start"></ion-icon>
					Try Again
				</ion-button>
			</div>

			<div v-else-if="filteredReports.length === 0" class="empty-state">
				<ion-icon :icon="bagOutline" class="empty-icon"></ion-icon>
				<h2>No Reports Found</h2>
				<p v-if="activeFiltersCount > 0">
					No reports match the current filter criteria.
				</p>
				<p v-else>
					No reports created yet. Be the first and report a found or lost item!
				</p>
				<ion-button
					v-if="activeFiltersCount === 0"
					fill="outline"
					@click="navigateToReportCreation">
					<ion-icon :icon="addOutline" slot="start"></ion-icon>
					Create First Report
				</ion-button>
				<ion-button v-else fill="outline" @click="clearAllFilters">
					Clear Filters
				</ion-button>
			</div>

			<div v-else class="reports-grid">
				<ion-card
					v-for="(report, index) in filteredReports"
					:key="report.id"
					class="report-card"
					:class="getStatusClass(report.status)"
					:style="{ '--animation-delay': `${index * 0.1}s` }"
					@click="navigateToReport(report.id)">
					<ion-card-header class="card-header">
						<div class="header-content">
							<ion-card-title class="report-title">{{
								report.title
							}}</ion-card-title>
							<ion-chip
								:color="getStatusColor(report.status)"
								class="status-chip">
								<ion-icon
									:icon="getStatusIcon(report.status)"
									class="chip-icon"></ion-icon>
								{{ getStatusText(report.status) }}
							</ion-chip>
						</div>
						<div class="report-details">
							<div class="detail-item">
								<ion-icon
									:icon="locationOutline"
									class="detail-icon"></ion-icon>
								{{ report.location }}
							</div>
							<div class="detail-item">
								<ion-icon :icon="timeOutline" class="detail-icon"></ion-icon>
								{{ getTimeAgo(report.dateCreated) }}
							</div>
						</div>
					</ion-card-header>

					<ion-card-content class="card-content">
						<div v-if="report.imageUrl" class="report-image-container">
							<img
								:src="report.imageUrl"
								:alt="report.title"
								class="report-image"
								@error="handleImageError" />
						</div>
						<div v-else class="no-image-placeholder">
							<ion-icon :icon="imageOutline" class="no-image-icon"></ion-icon>
							<span>No image available</span>
						</div>

						<p v-if="report.description" class="description">
							{{ report.description }}
						</p>

						<div class="metadata">
							<div class="metadata-item">
								<ion-icon
									:icon="fingerPrintOutline"
									class="metadata-icon"></ion-icon>
								<span class="metadata-text">Report #{{ report.id }}</span>
							</div>
							<div class="metadata-item">
								<ion-icon
									:icon="calendarOutline"
									class="metadata-icon"></ion-icon>
								<span class="metadata-text">{{
									formatDate(report.dateCreated)
								}}</span>
							</div>
							<div v-if="report.reporterName" class="metadata-item">
								<ion-icon
									:icon="personOutline"
									class="metadata-icon"></ion-icon>
								<span class="metadata-text"
									>Reported by {{ report.reporterName }}</span
								>
							</div>
						</div>
					</ion-card-content>

					<div class="card-actions">
						<ion-button
							fill="clear"
							size="small"
							@click.stop="navigateToReport(report.id)">
							<ion-icon :icon="eyeOutline" slot="start"></ion-icon>
							View Report
						</ion-button>

						<ion-button
							v-if="report.status.toUpperCase() === 'FOUND'"
							fill="solid"
							size="small"
							color="success"
							class="claim-button"
							@click.stop="showClaimModal(report)">
							<ion-icon :icon="handRightOutline" slot="start"></ion-icon>
							Claim Item
						</ion-button>

						<ion-button
							v-else-if="report.status.toUpperCase() === 'CLAIMED'"
							fill="clear"
							size="small"
							color="medium"
							disabled>
							<ion-icon :icon="checkmarkCircleOutline" slot="start"></ion-icon>
							Already Claimed
						</ion-button>

						<ion-button
							fill="clear"
							size="small"
							@click.stop="editReport(report.id)">
							<ion-icon :icon="createOutline" slot="start"></ion-icon>
							Edit
						</ion-button>
					</div>
				</ion-card>
			</div>
		</div>

		<ion-modal :is-open="showClaimModalOpen" @did-dismiss="closeClaimModal">
			<ion-header>
				<ion-toolbar>
					<ion-title>Claim Item</ion-title>
					<ion-buttons slot="end">
						<ion-button @click="closeClaimModal">
							<ion-icon :icon="closeOutline"></ion-icon>
						</ion-button>
					</ion-buttons>
				</ion-toolbar>
			</ion-header>
			<ion-content class="claim-modal-content">
				<div v-if="selectedItemToClaim" class="claim-form">
					<div class="item-info-section">
						<h2>{{ selectedItemToClaim.title }}</h2>
						<p class="item-description">
							{{ selectedItemToClaim.description }}
						</p>
						<div class="item-details">
							<div class="detail-row">
								<ion-icon
									:icon="locationOutline"
									class="detail-icon"></ion-icon>
								<span>{{ selectedItemToClaim.location }}</span>
							</div>
							<div class="detail-row">
								<ion-icon :icon="timeOutline" class="detail-icon"></ion-icon>
								<span
									>Reported
									{{ getTimeAgo(selectedItemToClaim.dateCreated) }}</span
								>
							</div>
						</div>
					</div>

					<div class="claim-form-section">
						<h3>Pickup Confirmation</h3>
						<p class="form-description">
							Please confirm that you want to claim this item. The finder will
							be notified and can contact you.
						</p>

						<div class="input-group">
							<ion-item
								class="modern-item"
								:class="{
									'item-filled': claimData.claimerName,
									'item-error': claimErrors.claimerName,
								}">
								<ion-label position="stacked" class="custom-label">
									<ion-icon :icon="personOutline" class="label-icon"></ion-icon>
									Your Name *
								</ion-label>
								<ion-input
									v-model="claimData.claimerName"
									placeholder="First and Last Name"
									class="custom-input"
									@ionBlur="validateClaimField('claimerName')"></ion-input>
							</ion-item>
							<div v-if="claimErrors.claimerName" class="error-message">
								<ion-icon :icon="alertCircleOutline"></ion-icon>
								{{ claimErrors.claimerName }}
							</div>
						</div>

						<div class="input-group">
							<ion-item
								class="modern-item"
								:class="{
									'item-filled': claimData.contactInfo,
									'item-error': claimErrors.contactInfo,
								}">
								<ion-label position="stacked" class="custom-label">
									<ion-icon :icon="mailOutline" class="label-icon"></ion-icon>
									Contact Information *
								</ion-label>
								<ion-input
									v-model="claimData.contactInfo"
									placeholder="Email or phone number"
									class="custom-input"
									@ionBlur="validateClaimField('contactInfo')"></ion-input>
							</ion-item>
							<div v-if="claimErrors.contactInfo" class="error-message">
								<ion-icon :icon="alertCircleOutline"></ion-icon>
								{{ claimErrors.contactInfo }}
							</div>
						</div>

						<div class="input-group">
							<ion-item class="modern-item textarea-item">
								<ion-label position="stacked" class="custom-label">
									<ion-icon
										:icon="documentTextOutline"
										class="label-icon"></ion-icon>
									Additional Information (optional)
								</ion-label>
								<ion-textarea
									v-model="claimData.additionalInfo"
									placeholder="Why do you think this is your item? Describe special features..."
									class="custom-textarea"
									rows="3"></ion-textarea>
							</ion-item>
						</div>

						<div class="verification-section">
							<h4>Verification</h4>
							<div class="checklist">
								<ion-item class="checklist-item">
									<ion-checkbox
										v-model="claimData.confirmOwnership"
										slot="start"></ion-checkbox>
									<ion-label> I confirm that this is my lost item </ion-label>
								</ion-item>
								<ion-item class="checklist-item">
									<ion-checkbox
										v-model="claimData.confirmContact"
										slot="start"></ion-checkbox>
									<ion-label> I agree to be contacted by the finder </ion-label>
								</ion-item>
							</div>
						</div>

						<div class="modal-actions">
							<ion-button
								expand="block"
								fill="outline"
								@click="closeClaimModal"
								:disabled="isSubmittingClaim">
								Cancel
							</ion-button>
							<ion-button
								expand="block"
								@click="submitClaim"
								:disabled="!isClaimFormValid || isSubmittingClaim"
								color="success">
								<ion-spinner
									v-if="isSubmittingClaim"
									name="crescent"
									class="spinner"></ion-spinner>
								<ion-icon
									v-else
									:icon="handRightOutline"
									slot="start"></ion-icon>
								{{ isSubmittingClaim ? 'Processing...' : 'Confirm Claim' }}
							</ion-button>
						</div>
					</div>
				</div>
			</ion-content>
		</ion-modal>

		<ion-modal
			:is-open="showFilterModal"
			@did-dismiss="showFilterModal = false">
			<ion-header>
				<ion-toolbar>
					<ion-title>Filter Reports</ion-title>
					<ion-buttons slot="end">
						<ion-button @click="showFilterModal = false">
							<ion-icon :icon="closeOutline"></ion-icon>
						</ion-button>
					</ion-buttons>
				</ion-toolbar>
			</ion-header>
			<ion-content class="filter-modal-content">
				<div class="filter-section">
					<h3>Status</h3>
					<ion-radio-group v-model="selectedStatus">
						<ion-item>
							<ion-radio slot="start" value=""></ion-radio>
							<ion-label>All Status</ion-label>
						</ion-item>
						<ion-item>
							<ion-radio slot="start" value="FOUND"></ion-radio>
							<ion-label>
								<ion-icon
									:icon="eyeOutline"
									style="margin-right: 8px"></ion-icon>
								Found
							</ion-label>
						</ion-item>
						<ion-item>
							<ion-radio slot="start" value="LOST"></ion-radio>
							<ion-label>
								<ion-icon
									:icon="searchOutline"
									style="margin-right: 8px"></ion-icon>
								Lost
							</ion-label>
						</ion-item>
						<ion-item>
							<ion-radio slot="start" value="CLAIMED"></ion-radio>
							<ion-label>
								<ion-icon
									:icon="checkmarkOutline"
									style="margin-right: 8px"></ion-icon>
								Claimed
							</ion-label>
						</ion-item>
					</ion-radio-group>
				</div>

				<div class="filter-section">
					<h3>Location</h3>
					<ion-radio-group v-model="selectedLocation">
						<ion-item>
							<ion-radio slot="start" value=""></ion-radio>
							<ion-label>All Locations</ion-label>
						</ion-item>
						<ion-item v-for="location in uniqueLocations" :key="location">
							<ion-radio slot="start" :value="location"></ion-radio>
							<ion-label>{{ location }}</ion-label>
						</ion-item>
					</ion-radio-group>
				</div>

				<div class="filter-actions">
					<ion-button expand="block" fill="outline" @click="clearAllFilters">
						Clear All Filters
					</ion-button>
					<ion-button expand="block" @click="applyFilters">
						Apply Filters
					</ion-button>
				</div>
			</ion-content>
		</ion-modal>
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
	IonCardContent,
	IonButton,
	IonIcon,
	IonChip,
	IonBadge,
	IonSpinner,
	IonModal,
	IonHeader,
	IonToolbar,
	IonTitle,
	IonButtons,
	IonContent,
	IonRadioGroup,
	IonRadio,
	IonItem,
	IonLabel,
	IonInput,
	IonTextarea,
	IonCheckbox,
} from '@ionic/vue';
import {
	bagOutline,
	addOutline,
	funnelOutline,
	closeOutline,
	locationOutline,
	timeOutline,
	refreshOutline,
	eyeOutline,
	handRightOutline,
	createOutline,
	checkmarkCircleOutline,
	alertCircleOutline,
	searchOutline,
	imageOutline,
	fingerPrintOutline,
	calendarOutline,
	personOutline,
	flagOutline,
	checkmarkOutline,
	documentTextOutline,
	mailOutline,
} from 'ionicons/icons';
import { ref, computed, watch, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useItemStore } from '@/stores/itemStore';
import type { Item } from '@/models/item';

interface VirtualReport {
	id: number;
	title: string;
	description: string;
	status: string;
	location: string;
	dateCreated: string;
	imageUrl?: string;
	reporterName?: string;
}

const router = useRouter();
const itemStore = useItemStore();
const activeTab = ref('items');
const searchTerm = ref('');
const showFilterModal = ref(false);
const selectedStatus = ref('');
const selectedLocation = ref('');

const items = computed(() => itemStore.getItems || []);
const isLoading = computed(() => itemStore.isLoading);
const error = computed(() => itemStore.getError);

const reports = computed((): VirtualReport[] => {
	return items.value.map((item: Item) => ({
		id: item.id,
		title: item.name,
		description: item.description,
		status: item.status || 'UNKNOWN',
		location: item.location,
		dateCreated: item.createdAt,
		imageUrl: item.imageUrl,
		reporterName: 'Unbekannt',
	}));
});

onMounted(async () => {
	await loadReports();
});

const loadReports = async () => {
	try {
		await itemStore.fetchItems();
	} catch (error) {
		console.error('Error loading reports:', error);
	}
};

const filteredReports = computed(() => {
	let filtered = reports.value;

	if (searchTerm.value.trim()) {
		const search = searchTerm.value.toLowerCase();
		filtered = filtered.filter(
			(report) =>
				report.title.toLowerCase().includes(search) ||
				report.description.toLowerCase().includes(search) ||
				report.location.toLowerCase().includes(search)
		);
	}

	if (selectedStatus.value) {
		filtered = filtered.filter(
			(report) =>
				report.status.toUpperCase() === selectedStatus.value.toUpperCase()
		);
	}

	if (selectedLocation.value) {
		filtered = filtered.filter(
			(report) => report.location === selectedLocation.value
		);
	}

	return filtered;
});

const uniqueLocations = computed(() => {
	return [
		...new Set(reports.value.map((report) => report.location).filter(Boolean)),
	].sort();
});

const foundReportsCount = computed(() => {
	return filteredReports.value.filter(
		(report) => report.status.toUpperCase() === 'FOUND'
	).length;
});

const lostReportsCount = computed(() => {
	return filteredReports.value.filter(
		(report) => report.status.toUpperCase() === 'LOST'
	).length;
});

const claimedReportsCount = computed(() => {
	return filteredReports.value.filter(
		(report) => report.status.toUpperCase() === 'CLAIMED'
	).length;
});

const activeFiltersCount = computed(() => {
	let count = 0;
	if (selectedStatus.value) count++;
	if (selectedLocation.value) count++;
	return count;
});

const getStatusText = (status: string) => {
	switch (status.toUpperCase()) {
		case 'FOUND':
			return 'Found';
		case 'LOST':
			return 'Lost';
		case 'CLAIMED':
			return 'Claimed';
		case 'RETURNED':
			return 'Returned';
		default:
			return status;
	}
};

const getStatusColor = (status: string) => {
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

const getStatusIcon = (status: string) => {
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

const getStatusClass = (status: string) => {
	return `status-${status.toLowerCase()}`;
};

const getTimeAgo = (dateString: string) => {
	const date = new Date(dateString);
	const now = new Date();
	const diffInHours = Math.floor(
		(now.getTime() - date.getTime()) / (1000 * 60 * 60)
	);

	if (diffInHours < 1) return 'A few minutes ago';
	if (diffInHours < 24) return `${diffInHours} hours ago`;

	const diffInDays = Math.floor(diffInHours / 24);
	if (diffInDays < 7) return `${diffInDays} days ago`;

	return date.toLocaleDateString('en-US');
};

const formatDate = (dateString: string) => {
	return new Date(dateString).toLocaleDateString('en-US', {
		day: '2-digit',
		month: '2-digit',
		year: 'numeric',
		hour: '2-digit',
		minute: '2-digit',
	});
};

const toggleFilterModal = () => {
	showFilterModal.value = !showFilterModal.value;
};

const clearStatusFilter = () => {
	selectedStatus.value = '';
};

const clearLocationFilter = () => {
	selectedLocation.value = '';
};

const clearAllFilters = () => {
	selectedStatus.value = '';
	selectedLocation.value = '';
	showFilterModal.value = false;
};

const applyFilters = () => {
	showFilterModal.value = false;
};

const navigateToReport = (reportId: number) => {
	router.push(`/items/${reportId}`);
};

const navigateToReportCreation = () => {
	router.push('/items/report');
};

const editReport = (reportId: number) => {
	router.push(`/items/${reportId}/edit`);
};

const showClaimModalOpen = ref(false);
const selectedItemToClaim = ref<VirtualReport | null>(null);
const isSubmittingClaim = ref(false);

const claimData = ref({
	claimerName: '',
	contactInfo: '',
	additionalInfo: '',
	confirmOwnership: false,
	confirmContact: false,
});

const claimErrors = ref({
	claimerName: '',
	contactInfo: '',
});

const isClaimFormValid = computed(() => {
	return (
		claimData.value.claimerName.trim() !== '' &&
		claimData.value.contactInfo.trim() !== '' &&
		claimData.value.confirmOwnership &&
		claimData.value.confirmContact &&
		!claimErrors.value.claimerName &&
		!claimErrors.value.contactInfo
	);
});

const showClaimModal = (report: VirtualReport) => {
	selectedItemToClaim.value = report;
	claimData.value = {
		claimerName: '',
		contactInfo: '',
		additionalInfo: '',
		confirmOwnership: false,
		confirmContact: false,
	};
	claimErrors.value = {
		claimerName: '',
		contactInfo: '',
	};
	showClaimModalOpen.value = true;
};

const closeClaimModal = () => {
	showClaimModalOpen.value = false;
	selectedItemToClaim.value = null;
};

const validateClaimField = (fieldName: keyof typeof claimErrors.value) => {
	const value =
		claimData.value[fieldName as keyof typeof claimData.value]
			?.toString()
			.trim() || '';

	switch (fieldName) {
		case 'claimerName':
			if (!value) {
				claimErrors.value.claimerName = 'Name is required';
			} else if (value.length < 2) {
				claimErrors.value.claimerName =
					'Name must be at least 2 characters long';
			} else {
				claimErrors.value.claimerName = '';
			}
			break;
		case 'contactInfo':
			if (!value) {
				claimErrors.value.contactInfo = 'Contact information is required';
			} else if (value.length < 5) {
				claimErrors.value.contactInfo = 'Enter valid contact information';
			} else {
				claimErrors.value.contactInfo = '';
			}
			break;
	}
};

const submitClaim = async () => {
	if (!selectedItemToClaim.value || !isClaimFormValid.value) return;

	try {
		isSubmittingClaim.value = true;

		const claimDescription = [
			'--- CLAIM REQUESTED ---',
			`Requested by: ${claimData.value.claimerName}`,
			`Contact: ${claimData.value.contactInfo}`,
			`Request Date: ${new Date().toLocaleDateString('en-US')}`,
			'',
			claimData.value.additionalInfo
				? `Additional Information: ${claimData.value.additionalInfo}`
				: '',
			'',
			'--- ORIGINAL DESCRIPTION ---',
			selectedItemToClaim.value.description,
		]
			.filter(Boolean)
			.join('\n');

		await itemStore.updateItem(selectedItemToClaim.value.id, {
			status: 'CLAIMED',
			description: claimDescription,
		});

		console.log('Item successfully claimed');

		closeClaimModal();

		await loadReports();
	} catch (error) {
		console.error('Error claiming item:', error);
	} finally {
		isSubmittingClaim.value = false;
	}
};

watch(activeTab, (tab) => {
	if (tab === 'locations') {
		router.push('/locations/overview');
	}
});
</script>

<style scoped>
.reports-container {
	padding: 16px;
	min-height: 100vh;
}

.search-and-filter {
	display: flex;
	gap: 12px;
	align-items: center;
	margin-bottom: 16px;
}

.custom-searchbar {
	flex: 1;
	--background: var(--ion-color-light);
	--border-radius: 12px;
	--box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-button {
	position: relative;
	--border-radius: 12px;
}

.filter-badge {
	position: absolute;
	top: -8px;
	right: -8px;
	background: var(--ion-color-primary);
	color: white;
	border-radius: 50%;
	min-width: 20px;
	height: 20px;
	font-size: 12px;
}

.filter-chips {
	display: flex;
	flex-wrap: wrap;
	gap: 8px;
	margin-bottom: 16px;
}

.filter-chip {
	--background: var(--ion-color-primary-tint);
	cursor: pointer;
}

.clear-all-button {
	--color: var(--ion-color-medium);
	font-size: 0.9em;
}

.stats-summary {
	display: flex;
	justify-content: space-around;
	background: white;
	border-radius: 12px;
	padding: 16px;
	margin-bottom: 20px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.stat-item {
	display: flex;
	align-items: center;
	gap: 8px;
	font-size: 0.9em;
	font-weight: 500;
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

.reports-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
	gap: 16px;
	animation: fadeInUp 0.6s ease-out;
}

.report-card {
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

.report-card:hover {
	transform: translateY(-2px);
	box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.card-header {
	background: linear-gradient(
		135deg,
		var(--ion-color-primary),
		var(--ion-color-primary-shade)
	);
	color: white;
	position: relative;
	overflow: hidden;
}

.card-header.status-found {
	background: linear-gradient(
		135deg,
		var(--ion-color-success),
		var(--ion-color-success-shade)
	);
}

.card-header.status-lost {
	background: linear-gradient(
		135deg,
		var(--ion-color-warning),
		var(--ion-color-warning-shade)
	);
}

.card-header.status-claimed {
	background: linear-gradient(
		135deg,
		var(--ion-color-medium),
		var(--ion-color-medium-shade)
	);
}

.card-header::before {
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

.report-card:hover .card-header::before {
	transform: rotate(45deg) translateX(100%);
}

.header-content {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	margin-bottom: 12px;
}

.report-title {
	color: white;
	font-size: 1.2em;
	font-weight: 600;
	margin: 0;
	flex: 1;
	margin-right: 12px;
}

.status-chip {
	--background: rgba(255, 255, 255, 0.2);
	--color: white;
	margin: 0;
}

.chip-icon {
	font-size: 14px;
	margin-right: 4px;
}

.report-details {
	display: flex;
	flex-direction: column;
	gap: 6px;
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

.report-image-container {
	margin-bottom: 12px;
	border-radius: 8px;
	overflow: hidden;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.report-image {
	width: 100%;
	height: 150px;
	object-fit: cover;
	transition: transform 0.3s ease;
}

.report-image:hover {
	transform: scale(1.05);
}

.no-image-placeholder {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	height: 100px;
	background: var(--ion-color-light-tint);
	border-radius: 8px;
	margin-bottom: 12px;
	color: var(--ion-color-medium);
}

.no-image-icon {
	font-size: 32px;
	margin-bottom: 8px;
}

.no-image-placeholder span {
	font-size: 0.8em;
	text-align: center;
}

.description {
	color: var(--ion-color-dark);
	line-height: 1.4;
	margin-bottom: 16px;
	font-size: 0.9em;
	display: -webkit-box;
	-webkit-line-clamp: 3;
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
	gap: 8px;
}

.filter-modal-content {
	padding: 20px;
}

.filter-section {
	margin-bottom: 30px;
}

.filter-section h3 {
	color: var(--ion-color-dark);
	margin-bottom: 16px;
	font-weight: 600;
}

.filter-actions {
	margin-top: 40px;
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.claim-button {
	--background: var(--ion-color-success);
	--color: white;
	font-weight: 600;
	animation: pulse 2s infinite;
}

@keyframes pulse {
	0% {
		transform: scale(1);
	}
	50% {
		transform: scale(1.05);
	}
	100% {
		transform: scale(1);
	}
}

.claim-modal-content {
	padding: 20px;
}

.claim-form {
	max-width: 500px;
	margin: 0 auto;
}

.item-info-section {
	background: var(--ion-color-light-tint);
	border-radius: 12px;
	padding: 20px;
	margin-bottom: 24px;
	border-left: 4px solid var(--ion-color-success);
}

.item-info-section h2 {
	color: var(--ion-color-dark);
	margin: 0 0 8px 0;
	font-weight: 600;
}

.item-description {
	color: var(--ion-color-medium-shade);
	margin: 0 0 16px 0;
	line-height: 1.4;
}

.item-details {
	display: flex;
	flex-direction: column;
	gap: 8px;
}

.detail-row {
	display: flex;
	align-items: center;
	gap: 8px;
	color: var(--ion-color-medium);
	font-size: 0.9em;
}

.claim-form-section h3 {
	color: var(--ion-color-dark);
	margin: 0 0 8px 0;
	font-weight: 600;
}

.form-description {
	color: var(--ion-color-medium);
	margin: 0 0 20px 0;
	line-height: 1.4;
	font-size: 0.9em;
}

.input-group {
	margin-bottom: 20px;
}

.modern-item {
	--background: var(--ion-color-light-tint);
	--border-radius: 8px;
	border: 2px solid var(--ion-color-light-shade);
	border-radius: 8px;
	margin-bottom: 8px;
	transition: border-color 0.3s ease;
}

.modern-item.item-filled {
	border-color: var(--ion-color-success-tint);
	--background: rgba(var(--ion-color-success-rgb), 0.05);
}

.modern-item.item-error {
	border-color: var(--ion-color-danger);
	--background: rgba(var(--ion-color-danger-rgb), 0.05);
}

.modern-item:focus-within {
	border-color: var(--ion-color-success);
	box-shadow: 0 0 0 3px rgba(var(--ion-color-success-rgb), 0.1);
}

.custom-label {
	display: flex;
	align-items: center;
	gap: 8px;
	font-weight: 600;
	color: var(--ion-color-dark);
}

.label-icon {
	font-size: 16px;
	color: var(--ion-color-success);
}

.custom-textarea {
	min-height: 60px;
}

.textarea-item {
	--padding-bottom: 12px;
}

.error-message {
	display: flex;
	align-items: center;
	gap: 6px;
	color: var(--ion-color-danger);
	font-size: 0.85em;
	margin-top: 4px;
}

.verification-section {
	background: var(--ion-color-light-tint);
	border-radius: 8px;
	padding: 16px;
	margin: 20px 0;
}

.verification-section h4 {
	color: var(--ion-color-dark);
	margin: 0 0 12px 0;
	font-weight: 600;
}

.checklist {
	display: flex;
	flex-direction: column;
	gap: 8px;
}

.checklist-item {
	--background: transparent;
	--padding-start: 0;
	--padding-end: 0;
	--min-height: auto;
}

.modal-actions {
	display: flex;
	flex-direction: column;
	gap: 12px;
	margin-top: 24px;
}

.spinner {
	width: 16px;
	height: 16px;
	margin-right: 8px;
}

@media (max-width: 768px) {
	.reports-grid {
		grid-template-columns: 1fr;
	}

	.reports-container {
		padding: 12px;
	}

	.search-and-filter {
		flex-direction: column;
		gap: 12px;
	}

	.filter-button {
		align-self: stretch;
	}

	.stats-summary {
		flex-direction: column;
		gap: 12px;
	}

	.card-actions {
		flex-direction: column;
		gap: 8px;
	}

	.claim-modal-content {
		padding: 16px;
	}

	.modal-actions {
		gap: 8px;
	}
}

@media (min-width: 1200px) {
	.reports-grid {
		grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
	}
}
</style>
