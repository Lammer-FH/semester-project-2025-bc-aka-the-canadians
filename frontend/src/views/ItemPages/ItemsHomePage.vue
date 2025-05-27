<template>
	<template-page addButtonPath="/items/report" addButtonText="Gegenstand melden">
		<template #header>
			<ion-toolbar>
				<navigation-tabs v-model="activeTab" class="full-width-tabs" />
			</ion-toolbar>
			<ion-toolbar>
				<div class="search-and-filter">
					<ion-searchbar 
						v-model="searchTerm" 
						placeholder="Gegenstände durchsuchen..." 
						show-clear-button="focus"
						:debounce="300"
						class="custom-searchbar"
					/>
					<ion-button 
						fill="clear" 
						@click="toggleFilterModal"
						class="filter-button"
					>
						<ion-icon :icon="funnelOutline" slot="icon-only"></ion-icon>
						<ion-badge 
							v-if="activeFiltersCount > 0" 
							color="primary" 
							class="filter-badge"
						>
							{{ activeFiltersCount }}
						</ion-badge>
					</ion-button>
				</div>
			</ion-toolbar>
		</template>

		<div v-if="activeTab === 'items'" class="items-container">
			<!-- Filter Chips -->
			<div v-if="activeFiltersCount > 0" class="filter-chips">
				<ion-chip 
					v-if="selectedStatus" 
					color="primary" 
					@click="clearStatusFilter"
					class="filter-chip"
				>
					{{ getStatusText(selectedStatus) }}
					<ion-icon :icon="closeOutline" slot="end"></ion-icon>
				</ion-chip>
				<ion-chip 
					v-if="selectedLocation" 
					color="secondary" 
					@click="clearLocationFilter"
					class="filter-chip"
				>
					{{ selectedLocation }}
					<ion-icon :icon="closeOutline" slot="end"></ion-icon>
				</ion-chip>
				<ion-button 
					v-if="activeFiltersCount > 1" 
					fill="clear" 
					size="small" 
					@click="clearAllFilters"
					class="clear-all-button"
				>
					Alle Filter entfernen
				</ion-button>
			</div>



			<!-- Empty State -->
			<div v-if="filteredItems.length === 0" class="empty-state">
				<ion-icon :icon="bagOutline" class="empty-icon"></ion-icon>
				<h2>Keine Gegenstände gefunden</h2>
				<p v-if="searchTerm || activeFiltersCount > 0">
					Versuche deine Suchkriterien zu ändern oder entferne Filter.
				</p>
				<p v-else>
					Noch keine Gegenstände vorhanden. Melde den ersten Gegenstand!
				</p>
				<ion-button 
					v-if="!searchTerm && activeFiltersCount === 0" 
					fill="solid" 
					color="primary" 
					@click="$router.push('/items/report')"
					class="ion-margin-top"
				>
					<ion-icon :icon="addOutline" slot="start"></ion-icon>
					Ersten Gegenstand melden
				</ion-button>
			</div>

			<!-- Items Grid -->
			<div v-else class="items-grid">
				<ion-card
					v-for="(item, index) in filteredItems"
					:key="item.id"
					class="item-card"
					:style="{ '--animation-delay': `${index * 0.1}s` }"
					@click="navigateToItem(item.id)"
					button
				>
					<div class="card-header" :class="getStatusClass(item.status)">
						<ion-card-header>
							<div class="header-content">
								<ion-card-title class="item-name">
									{{ item.name }}
								</ion-card-title>
								<ion-chip 
									:color="getStatusColor(item.status)" 
									class="status-chip"
								>
									<ion-icon :icon="getStatusIcon(item.status)" class="chip-icon"></ion-icon>
									{{ getStatusText(item.status) }}
								</ion-chip>
							</div>
							<ion-card-subtitle class="item-details">
								<div class="detail-item">
									<ion-icon :icon="locationOutline" class="detail-icon"></ion-icon>
									<span>{{ item.location }}</span>
								</div>
							</ion-card-subtitle>
						</ion-card-header>
					</div>

					<ion-card-content class="card-content">
						

						<p class="description">{{ item.description }}</p>
						
						<div class="metadata">
							<div class="metadata-item">
								<ion-icon :icon="timeOutline" class="metadata-icon"></ion-icon>
								<span class="metadata-text">
									{{ getTimeAgo(item.createdAt) }}
								</span>
							</div>
							<div class="metadata-item" v-if="item.updatedAt !== item.createdAt">
								<ion-icon :icon="refreshOutline" class="metadata-icon"></ion-icon>
								<span class="metadata-text">
									Aktualisiert {{ getTimeAgo(item.updatedAt) }}
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
							@click.stop="navigateToItem(item.id)"
						>
							<ion-icon :icon="eyeOutline" slot="start"></ion-icon>
							Details
						</ion-button>
						<ion-button 
							v-if="item.status === 'found'" 
							fill="clear" 
							size="small" 
							color="success"
							@click.stop="claimItem(item.id)"
						>
							<ion-icon :icon="handRightOutline" slot="start"></ion-icon>
							Abholen
						</ion-button>
						<ion-button 
							v-else
							fill="clear" 
							size="small" 
							color="medium"
							@click.stop="editItem(item.id)"
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
				<p>Lade Gegenstände...</p>
			</div>
		</div>

		<!-- Filter Modal -->
		<ion-modal :is-open="showFilterModal" @will-dismiss="showFilterModal = false">
			<ion-header>
				<ion-toolbar>
					<ion-title>Filter</ion-title>
					<ion-buttons slot="end">
						<ion-button @click="showFilterModal = false">
							<ion-icon :icon="closeOutline" slot="icon-only"></ion-icon>
						</ion-button>
					</ion-buttons>
				</ion-toolbar>
			</ion-header>
			<ion-content class="filter-modal-content">
				<div class="filter-section">
					<h3>Status</h3>
					<ion-radio-group v-model="selectedStatus">
						<ion-item>
							<ion-radio value="" slot="start"></ion-radio>
							<ion-label>Alle Status</ion-label>
						</ion-item>
						<ion-item>
							<ion-radio value="lost" slot="start"></ion-radio>
							<ion-label>Verloren</ion-label>
						</ion-item>
						<ion-item>
							<ion-radio value="found" slot="start"></ion-radio>
							<ion-label>Gefunden</ion-label>
						</ion-item>
						<ion-item>
							<ion-radio value="claimed" slot="start"></ion-radio>
							<ion-label>Abgeholt</ion-label>
						</ion-item>
					</ion-radio-group>
				</div>

				<div class="filter-section">
					<h3>Standort</h3>
					<ion-radio-group v-model="selectedLocation">
						<ion-item>
							<ion-radio value="" slot="start"></ion-radio>
							<ion-label>Alle Standorte</ion-label>
						</ion-item>
						<ion-item v-for="location in uniqueLocations" :key="location">
							<ion-radio :value="location" slot="start"></ion-radio>
							<ion-label>{{ location }}</ion-label>
						</ion-item>
					</ion-radio-group>
				</div>

				<div class="filter-actions">
					<ion-button 
						fill="outline" 
						expand="block" 
						@click="clearAllFilters"
						class="ion-margin-bottom"
					>
						Alle Filter zurücksetzen
					</ion-button>
					<ion-button 
						fill="solid" 
						expand="block" 
						@click="applyFilters"
					>
						Filter anwenden
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
	IonCardSubtitle,
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
	shirtOutline,
	imageOutline,
} from 'ionicons/icons';
import { ref, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import type { Item } from '@/models/item';

const router = useRouter();
const activeTab = ref('items');
const searchTerm = ref('');
const isLoading = ref(false);
const showFilterModal = ref(false);
const selectedStatus = ref('');
const selectedLocation = ref('');

// Replace mock data with actual data later on
const items = ref<Item[]>([
	{
		id: 1,
		name: 'Schwarzer Rucksack',
		description: 'Großer schwarzer Rucksack mit Laptop-Fach, Marke Eastpak',
		location: 'Bibliothek',
		status: 'lost',
		createdAt: '2024-01-15T10:00:00Z',
		updatedAt: '2024-01-15T10:00:00Z',
		imageUrl: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjE4MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KICA8cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjMzMzIi8+CiAgPHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNiIgZmlsbD0iI2ZmZiIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPlNjaHdhcnplciBSdWNrc2FjazwvdGV4dD4KPC9zdmc+',
	},
	{
		id: 2,
		name: 'Blaue Wasserflasche',
		description: 'Blaue Metallflasche mit Universitäts-Logo, 500ml',
		location: 'Cafeteria',
		status: 'found',
		createdAt: '2024-01-14T09:00:00Z',
		updatedAt: '2024-01-14T15:30:00Z',
		imageUrl: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjE4MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KICA8cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjMDA3Y2ZmIi8+CiAgPHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNiIgZmlsbD0iI2ZmZiIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPldhdGVyZmxhc2NoZTwvdGV4dD4KPC9zdmc+',
	},
	{
		id: 3,
		name: 'Rote Winterjacke',
		description: 'Rote North Face Jacke, Größe M, mit Kapuze',
		location: 'Sporthalle',
		status: 'found',
		createdAt: '2024-01-13T08:00:00Z',
		updatedAt: '2024-01-13T08:00:00Z',
		imageUrl: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjE4MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KICA8cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZGMzNTQ1Ii8+CiAgPHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNiIgZmlsbD0iI2ZmZiIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPlJvdGUgSmFja2U8L3RleHQ+Cjwvc3ZnPg==',
	},
	{
		id: 4,
		name: 'AirPods Pro',
		description: 'Apple AirPods Pro in weißer Ladehülle',
		location: 'Computerlabor',
		status: 'claimed',
		createdAt: '2024-01-12T14:00:00Z',
		updatedAt: '2024-01-13T10:00:00Z',
	},
	{
		id: 5,
		name: 'Schwarze Brille',
		description: 'Schwarze Brille mit rechteckigen Gläsern, Marke Ray-Ban',
		location: 'Bibliothek',
		status: 'lost',
		createdAt: '2024-01-11T16:00:00Z',
		updatedAt: '2024-01-11T16:00:00Z',
	},
]);

const filteredItems = computed(() => {
	return items.value.filter(item => {
		const matchesSearch = !searchTerm.value || 
			item.name.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
			item.description.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
			item.location.toLowerCase().includes(searchTerm.value.toLowerCase());
		
		const matchesStatus = !selectedStatus.value || item.status === selectedStatus.value;
		const matchesLocation = !selectedLocation.value || item.location === selectedLocation.value;
		
		return matchesSearch && matchesStatus && matchesLocation;
	});
});

const uniqueLocations = computed(() => {
	return [...new Set(items.value.map(item => item.location))].sort();
});

const foundItemsCount = computed(() => {
	return filteredItems.value.filter(item => item.status === 'found').length;
});

const lostItemsCount = computed(() => {
	return filteredItems.value.filter(item => item.status === 'lost').length;
});

const activeFiltersCount = computed(() => {
	let count = 0;
	if (selectedStatus.value) count++;
	if (selectedLocation.value) count++;
	return count;
});

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

const getTimeAgo = (dateString: string) => {
	const date = new Date(dateString);
	const now = new Date();
	const diffInHours = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60));
	
	if (diffInHours < 1) return 'Vor wenigen Minuten';
	if (diffInHours < 24) return `Vor ${diffInHours} Stunde${diffInHours > 1 ? 'n' : ''}`;
	
	const diffInDays = Math.floor(diffInHours / 24);
	if (diffInDays < 7) return `Vor ${diffInDays} Tag${diffInDays > 1 ? 'en' : ''}`;
	
	return date.toLocaleDateString('de-DE');
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

const navigateToItem = (itemId: number) => {
	router.push(`/items/${itemId}`);
};

const claimItem = (itemId: number) => {
	// TODO: Implement claim functionality
	console.log('Claiming item:', itemId);
};

const editItem = (itemId: number) => {
	router.push(`/items/${itemId}/edit`);
};

watch(activeTab, (tab) => {
	if (tab === 'locations') {
		router.push('/locations/home');
	}
});
</script>

<style scoped>
.items-container {
	padding: 16px;
	min-height: 100vh;
	background: var(--ion-color-light-tint);
}

.search-and-filter {
	display: flex;
	align-items: center;
	gap: 8px;
	padding: 8px 16px;
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
	width: 44px;
	height: 44px;
}

.filter-badge {
	position: absolute;
	top: 4px;
	right: 4px;
	font-size: 10px;
	min-width: 16px;
	height: 16px;
}

.filter-chips {
	display: flex;
	flex-wrap: wrap;
	gap: 8px;
	margin-bottom: 16px;
	align-items: center;
}

.filter-chip {
	cursor: pointer;
}

.clear-all-button {
	margin-left: 8px;
}



.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	text-align: center;
	padding: 60px 20px;
	margin-top: 40px;
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

.items-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
	gap: 12px;
	animation: fadeInUp 0.6s ease-out;
}

.item-card {
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

.item-card:hover {
	transform: translateY(-2px);
	box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.card-header {
	color: white;
	position: relative;
	overflow: hidden;
}

.card-header.status-found {
	background: linear-gradient(135deg, var(--ion-color-success-tint), var(--ion-color-success));
}

.card-header.status-lost {
	background: linear-gradient(135deg, var(--ion-color-warning-tint), var(--ion-color-warning));
}

.card-header.status-claimed {
	background: linear-gradient(135deg, var(--ion-color-medium-tint), var(--ion-color-medium));
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

.item-card:hover .card-header::before {
	transform: rotate(45deg) translateX(100%);
}

.header-content {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	margin-bottom: 8px;
}

.item-name {
	color: white;
	font-size: 1.2em;
	font-weight: 600;
	margin: 0;
	flex: 1;
}

.status-chip {
	--background: rgba(255, 255, 255, 0.2);
	--color: white;
	margin-left: 12px;
}

.chip-icon {
	font-size: 14px;
	margin-right: 4px;
}

.item-details {
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

.item-image-container {
	margin-bottom: 12px;
	border-radius: 8px;
	overflow: hidden;
}

.item-image {
	width: 100%;
	height: 120px;
	object-fit: cover;
	border-radius: 8px;
	transition: transform 0.3s ease;
}

.item-image:hover {
	transform: scale(1.02);
}

.no-image-placeholder {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	height: 120px;
	background: var(--ion-color-light-shade);
	border-radius: 8px;
	margin-bottom: 12px;
	color: var(--ion-color-medium);
}

.no-image-icon {
	font-size: 32px;
	margin-bottom: 8px;
	opacity: 0.6;
}

.no-image-placeholder span {
	font-size: 0.8em;
	font-weight: 500;
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
	gap: 6px;
	margin-top: 12px;
	padding-top: 12px;
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

.filter-modal-content {
	padding: 20px;
}

.filter-section {
	margin-bottom: 30px;
}

.filter-section h3 {
	color: var(--ion-color-dark);
	margin-bottom: 16px;
	font-size: 1.1em;
	font-weight: 600;
}

.filter-actions {
	margin-top: 40px;
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
	.items-grid {
		grid-template-columns: 1fr;
		gap: 12px;
	}
	
	.items-container {
		padding: 12px;
	}
	
	.header-content {
		flex-direction: column;
		align-items: flex-start;
		gap: 8px;
	}
	
	.status-chip {
		margin-left: 0;
	}
	
	.card-actions {
		flex-direction: column;
		gap: 8px;
	}
	

}

@media (min-width: 1200px) {
	.items-grid {
		grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
	}
}
</style>
