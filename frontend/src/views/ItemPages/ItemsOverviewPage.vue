<template>
  <template-page
    :headline="'Browse Items'"
    addButtonPath="/items/report"
    addButtonText="Report New Item"
  >
    <template #header>
      <NavigationTabs v-model="activeTab" />
    </template>

    <div class="items-container">
      <div class="search-and-filter">
        <ion-searchbar
          v-model="searchTerm"
          placeholder="Search for items..."
          :debounce="300"
          class="custom-searchbar"
        ></ion-searchbar>

        <ion-button
          fill="outline"
          size="small"
          class="filter-button"
          @click="toggleFilterModal"
        >
          <ion-icon :icon="funnelOutline" slot="start"></ion-icon>
          Filter
          <ion-badge v-if="activeFiltersCount > 0" class="filter-badge">
            {{ activeFiltersCount }}
          </ion-badge>
        </ion-button>
      </div>

      <div v-if="activeFiltersCount > 0" class="filter-chips">
        <ion-chip
          v-if="selectedClaimedStatus !== null"
          class="filter-chip"
          @click="clearClaimedStatusFilter"
        >
          <ion-icon :icon="personOutline"></ion-icon>
          <ion-label>{{
            getClaimedStatusText(selectedClaimedStatus)
          }}</ion-label>
          <ion-icon :icon="closeOutline"></ion-icon>
        </ion-chip>
      </div>

      <div class="stats-summary">
        <div class="stat-item">
          <ion-icon :icon="cubeOutline" color="primary"></ion-icon>
          <span>{{ filteredItems.length }} Total Items</span>
        </div>
        <div class="stat-item">
          <ion-icon :icon="checkmarkCircleOutline" color="success"></ion-icon>
          <span>{{ claimedItemsCount }} Claimed</span>
        </div>
        <div class="stat-item">
          <ion-icon :icon="searchOutline" color="warning"></ion-icon>
          <span>{{ unclaimedItemsCount }} Unclaimed</span>
        </div>
      </div>

      <div v-if="isLoading" class="loading-container">
        <ion-spinner name="crescent" color="primary"></ion-spinner>
        <p>Loading items...</p>
      </div>

      <div v-else-if="error" class="empty-state">
        <ion-icon :icon="alertCircleOutline" class="empty-icon"></ion-icon>
        <h2>Error Loading</h2>
        <p>{{ error }}</p>
        <ion-button @click="loadItems">
          <ion-icon :icon="refreshOutline" slot="start"></ion-icon>
          Try Again
        </ion-button>
      </div>

      <div v-else-if="filteredItems.length === 0" class="empty-state">
        <ion-icon :icon="cubeOutline" class="empty-icon"></ion-icon>
        <h2>No Items Found</h2>
        <p v-if="activeFiltersCount > 0">
          No items match the current filter criteria.
        </p>
        <p v-else>
          No items reported yet. Be the first to report a found or lost item!
        </p>
        <ion-button routerLink="/items/report" fill="solid">
          <ion-icon :icon="addOutline" slot="start"></ion-icon>
          Report First Item
        </ion-button>
      </div>

      <div v-else class="items-grid">
        <ion-card
          v-for="item in filteredItems"
          :key="item.id"
          class="item-card"
          @click="navigateToItem(item.id)"
        >
          <div
            class="card-header"
            :class="getClaimedStatusClass(item.claimedByUserId)"
          >
            <div class="header-content">
              <h3 class="item-title">{{ item.name }}</h3>
              <div class="status-chip">
                <ion-icon
                  :icon="getClaimedStatusIcon(item.claimedByUserId)"
                  class="chip-icon"
                ></ion-icon>
                <span>{{
                  getClaimedStatusText(item.claimedByUserId !== null)
                }}</span>
              </div>
            </div>
          </div>

          <ion-card-content class="card-content">
            <div v-if="item.description" class="description">
              <p>{{ truncateDescription(item.description) }}</p>
            </div>

            <div class="item-details">
              <div class="detail-item">
                <ion-icon
                  :icon="documentTextOutline"
                  class="detail-icon"
                ></ion-icon>
                <span>Report #{{ item.reportId }}</span>
              </div>
              <div v-if="item.claimedByUser" class="detail-item">
                <ion-icon :icon="personOutline" class="detail-icon"></ion-icon>
                <span
                  >Claimed by {{ item.claimedByUser.name || "Unknown" }}</span
                >
              </div>
              <div class="detail-item">
                <ion-icon :icon="timeOutline" class="detail-icon"></ion-icon>
                <span>{{ formatDate(item.createdAt) }}</span>
              </div>
            </div>

            <div class="metadata">
              <div class="metadata-item">
                <ion-icon
                  :icon="calendarOutline"
                  class="metadata-icon"
                ></ion-icon>
                <span class="metadata-text">{{
                  getTimeAgo(item.createdAt)
                }}</span>
              </div>
            </div>
          </ion-card-content>

          <div class="card-actions">
            <ion-button
              fill="clear"
              size="small"
              @click.stop="editItem(item.id)"
            >
              <ion-icon :icon="createOutline" slot="icon-only"></ion-icon>
            </ion-button>
            <ion-button
              v-if="!item.claimedByUserId"
              fill="solid"
              size="small"
              color="success"
              @click.stop="showClaimModal(item)"
            >
              <ion-icon :icon="handRightOutline" slot="start"></ion-icon>
              Claim Item
            </ion-button>
            <ion-button
              fill="clear"
              size="small"
              color="primary"
              @click.stop="navigateToItem(item.id)"
            >
              View Details
            </ion-button>
          </div>
        </ion-card>
      </div>

      <ion-modal
        :is-open="showFilterModal"
        @didDismiss="handleFilterModalDismiss"
        :backdrop-dismiss="true"
      >
        <ion-header>
          <ion-toolbar>
            <ion-title>Filter Items</ion-title>
            <ion-button
              slot="end"
              fill="clear"
              @click="handleFilterModalDismiss"
            >
              <ion-icon :icon="closeOutline"></ion-icon>
            </ion-button>
          </ion-toolbar>
        </ion-header>
        <ion-content class="filter-modal-content">
          <div class="filter-section">
            <h3>Claim Status</h3>
            <ion-radio-group v-model="selectedClaimedStatus">
              <div class="radio-item">
                <ion-radio :value="null">All Items</ion-radio>
              </div>
              <div class="radio-item">
                <ion-radio :value="false">Unclaimed Items</ion-radio>
              </div>
              <div class="radio-item">
                <ion-radio :value="true">Claimed Items</ion-radio>
              </div>
            </ion-radio-group>
          </div>

          <div class="filter-actions">
            <ion-button expand="block" @click="applyFilters">
              Apply Filters
            </ion-button>
          </div>
        </ion-content>
      </ion-modal>

      <ion-modal :is-open="showClaimModalOpen" @did-dismiss="closeClaimModal">
        <ion-header>
          <ion-toolbar>
            <ion-title>Claim Item</ion-title>
            <ion-button slot="end" fill="clear" @click="closeClaimModal">
              <ion-icon :icon="closeOutline"></ion-icon>
            </ion-button>
          </ion-toolbar>
        </ion-header>
        <ion-content class="claim-modal-content">
          <div v-if="selectedItemToClaim" class="claim-form">
            <div class="item-info-section">
              <h2>{{ selectedItemToClaim.name }}</h2>
              <p
                v-if="selectedItemToClaim.description"
                class="item-description"
              >
                {{ selectedItemToClaim.description }}
              </p>
              <div class="item-details">
                <div class="detail-row">
                  <strong>Report ID:</strong> {{ selectedItemToClaim.reportId }}
                </div>
                <div class="detail-row">
                  <strong>Reported:</strong>
                  {{ formatDate(selectedItemToClaim.createdAt) }}
                </div>
              </div>
            </div>

            <div class="claim-form-section">
              <h3>Claim Information</h3>
              <p class="form-description">
                Please provide details about why you believe this item belongs
                to you.
              </p>

              <div class="input-group">
                <ion-textarea
                  v-model="claimData.description"
                  label="Description *"
                  placeholder="Describe how you lost this item or provide identifying details..."
                  :rows="4"
                  class="custom-textarea"
                  @blur="validateClaimField('description')"
                  :class="{
                    'textarea-filled': claimData.description.trim(),
                    'textarea-error': claimErrors.description,
                  }"
                ></ion-textarea>
                <div v-if="claimErrors.description" class="error-message">
                  {{ claimErrors.description }}
                </div>
              </div>

              <div class="verification-section">
                <h4>Verification</h4>
                <div class="checklist">
                  <ion-checkbox v-model="claimData.hasProof">
                    I have proof of ownership or identifying details
                  </ion-checkbox>
                </div>
              </div>
            </div>

            <div class="modal-actions">
              <ion-button
                expand="block"
                @click="submitClaim"
                :disabled="!isClaimFormValid || isSubmittingClaim"
              >
                <ion-spinner
                  v-if="isSubmittingClaim"
                  name="crescent"
                  class="spinner"
                ></ion-spinner>
                <span v-else>Submit Claim</span>
              </ion-button>
            </div>
          </div>
        </ion-content>
      </ion-modal>
    </div>
  </template-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import {
  IonCard,
  IonCardContent,
  IonButton,
  IonIcon,
  IonSearchbar,
  IonBadge,
  IonChip,
  IonLabel,
  IonSpinner,
  IonModal,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonRadioGroup,
  IonRadio,
  IonItem,
  IonTextarea,
  IonCheckbox,
} from "@ionic/vue";
import {
  funnelOutline,
  personOutline,
  closeOutline,
  cubeOutline,
  checkmarkCircleOutline,
  searchOutline,
  alertCircleOutline,
  refreshOutline,
  addOutline,
  documentTextOutline,
  timeOutline,
  calendarOutline,
  createOutline,
  handRightOutline,
  chatbubbleOutline,
} from "ionicons/icons";
import { useItemStore } from "@/stores/itemStore";
import { Item } from "@/models/item";
import TemplatePage from "@/components/TemplatePage.vue";
import NavigationTabs from "@/components/NavigationTabs.vue";

const router = useRouter();
const itemStore = useItemStore();

const activeTab = ref("items");
const searchTerm = ref("");
const showFilterModal = ref(false);
const selectedClaimedStatus = ref<boolean | null>(null);

const items = computed(() => itemStore.getItems);
const isLoading = computed(() => itemStore.isLoading);
const error = computed(() => itemStore.getError);

const filteredItems = computed(() => {
  let filtered = items.value;

  if (searchTerm.value.trim()) {
    const search = searchTerm.value.toLowerCase();
    filtered = filtered.filter(
      item =>
        item.name.toLowerCase().includes(search) ||
        item.description?.toLowerCase().includes(search) ||
        item.id.toString().includes(search) ||
        item.reportId.toString().includes(search)
    );
  }

  if (selectedClaimedStatus.value !== null) {
    if (selectedClaimedStatus.value) {
      filtered = filtered.filter(item => item.claimedByUserId !== null);
    } else {
      filtered = filtered.filter(item => item.claimedByUserId === null);
    }
  }

  return filtered;
});

const claimedItemsCount = computed(
  () => items.value.filter(item => item.claimedByUserId !== null).length
);

const unclaimedItemsCount = computed(
  () => items.value.filter(item => item.claimedByUserId === null).length
);

const activeFiltersCount = computed(() => {
  let count = 0;
  if (selectedClaimedStatus.value !== null) count++;
  return count;
});

const showClaimModalOpen = ref(false);
const selectedItemToClaim = ref<Item | null>(null);
const isSubmittingClaim = ref(false);

const claimData = ref({
  description: "",
  hasProof: false,
});

const claimErrors = ref({
  description: "",
});

const isClaimFormValid = computed(() => {
  return (
    claimData.value.description.trim().length >= 10 &&
    claimData.value.hasProof &&
    !Object.values(claimErrors.value).some(error => error)
  );
});

const getClaimedStatusText = (isClaimed: boolean): string => {
  return isClaimed ? "Claimed" : "Unclaimed";
};

const getClaimedStatusIcon = (
  claimedByUserId: number | null | undefined
): string => {
  return claimedByUserId ? checkmarkCircleOutline : searchOutline;
};

const getClaimedStatusClass = (
  claimedByUserId: number | null | undefined
): string => {
  return claimedByUserId ? "status-claimed" : "status-unclaimed";
};

const getTimeAgo = (dateString: string): string => {
  const date = new Date(dateString);
  const now = new Date();
  const diffInMs = now.getTime() - date.getTime();
  const diffInHours = Math.floor(diffInMs / (1000 * 60 * 60));
  const diffInDays = Math.floor(diffInHours / 24);

  if (diffInDays > 0) {
    return `${diffInDays} day${diffInDays > 1 ? "s" : ""} ago`;
  } else if (diffInHours > 0) {
    return `${diffInHours} hour${diffInHours > 1 ? "s" : ""} ago`;
  } else {
    return "Less than an hour ago";
  }
};

const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleDateString("en-US", {
    year: "numeric",
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const truncateDescription = (
  description: string,
  maxLength: number = 120
): string => {
  if (description.length <= maxLength) return description;
  return description.substring(0, maxLength) + "...";
};

const toggleFilterModal = (): void => {
  showFilterModal.value = !showFilterModal.value;
};

const clearClaimedStatusFilter = (): void => {
  selectedClaimedStatus.value = null;
};

const applyFilters = (): void => {
  showFilterModal.value = false;
};

const handleFilterModalDismiss = (): void => {
  showFilterModal.value = false;
};

const navigateToItem = (itemId: number): void => {
  router.push(`/items/${itemId}`);
};

const editItem = (itemId: number): void => {
  router.push(`/items/${itemId}/edit`);
};

const loadItems = async (): Promise<void> => {
  try {
    await itemStore.fetchItems();
  } catch (error) {
    console.error("Error loading items:", error);
  }
};

const showClaimModal = (item: Item): void => {
  selectedItemToClaim.value = item;
  claimData.value = {
    description: "",
    hasProof: false,
  };
  claimErrors.value = {
    description: "",
  };
  showClaimModalOpen.value = true;
};

const closeClaimModal = (): void => {
  showClaimModalOpen.value = false;
  selectedItemToClaim.value = null;
};

const validateClaimField = (field: string): void => {
  switch (field) {
    case "description":
      if (claimData.value.description.trim().length < 10) {
        claimErrors.value.description =
          "Description must be at least 10 characters long";
      } else {
        claimErrors.value.description = "";
      }
      break;
  }
};

const submitClaim = async (): Promise<void> => {
  if (!selectedItemToClaim.value) return;

  try {
    isSubmittingClaim.value = true;

    // For now, we'll simulate claiming by updating the item
    // In a real implementation, you'd call a proper claim API
    const updatedItem = await itemStore.updateItem(
      selectedItemToClaim.value.id,
      {
        claimedByUserId: 1,
      }
    );

    if (updatedItem) {
      closeClaimModal();
      console.log("Item claimed successfully");
    }
  } catch (error) {
    console.error("Error claiming item:", error);
  } finally {
    isSubmittingClaim.value = false;
  }
};

onMounted(async () => {
  await loadItems();
});
</script>

<style scoped>
.items-container {
  padding: 16px;
  max-width: 1200px;
  margin: 0 auto;
}

.search-and-filter {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  align-items: center;
}

.custom-searchbar {
  flex: 1;
  --background: var(--ion-color-light);
  --border-radius: 12px;
}

.filter-button {
  --border-radius: 12px;
  position: relative;
}

.filter-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 18px;
  height: 18px;
  font-size: 10px;
}

.filter-chips {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.filter-chip {
  --background: var(--ion-color-primary-tint);
  cursor: pointer;
}

.clear-all-button {
  --color: var(--ion-color-medium);
}

.stats-summary {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  justify-content: space-around;
  padding: 16px;
  background: var(--ion-color-light);
  border-radius: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 48px 16px;
}

.loading-container p {
  color: var(--ion-color-medium);
  font-size: 14px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 48px 16px;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  color: var(--ion-color-medium);
}

.empty-state h2 {
  margin: 0;
  color: var(--ion-color-dark);
}

.empty-state p {
  margin: 0;
  color: var(--ion-color-medium);
}

.items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 16px;
}

.item-card {
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.item-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.card-header {
  padding: 16px;
  background: linear-gradient(
    135deg,
    var(--ion-color-primary) 0%,
    var(--ion-color-primary-shade) 100%
  );
  color: white;
  position: relative;
  overflow: hidden;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.item-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.status-chip {
  display: flex;
  align-items: center;
  gap: 4px;
  background: rgba(255, 255, 255, 0.2);
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.chip-icon {
  font-size: 16px;
}

.card-content {
  padding: 16px;
}

.description {
  margin-bottom: 16px;
}

.description p {
  margin: 0;
  color: var(--ion-color-dark);
  font-size: 14px;
  line-height: 1.4;
}

.item-details {
  margin-bottom: 16px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 14px;
}

.detail-icon {
  font-size: 16px;
  color: var(--ion-color-medium);
}

.metadata {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: var(--ion-color-medium);
  border-top: 1px solid var(--ion-color-light);
  padding-top: 12px;
}

.metadata-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.metadata-icon {
  font-size: 14px;
}

.metadata-text {
  font-size: 12px;
}

.card-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 16px 16px;
}

.filter-modal-content {
  padding: 16px;
}

.filter-section {
  margin-bottom: 24px;
}

.filter-section h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  font-weight: 600;
}

.radio-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid var(--ion-color-light);
}

.radio-item:last-child {
  border-bottom: none;
}

ion-radio {
  margin-right: 12px;
  font-size: 16px;
  font-weight: 500;
}

.filter-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 32px;
}

.claim-modal-content {
  padding: 16px;
}

.claim-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.item-info-section {
  padding: 16px;
  background: var(--ion-color-light);
  border-radius: 12px;
}

.item-info-section h2 {
  margin: 0 0 8px 0;
  color: var(--ion-color-dark);
}

.item-description {
  margin: 8px 0;
  color: var(--ion-color-medium);
  font-size: 14px;
}

.item-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-row {
  display: flex;
  gap: 8px;
  font-size: 14px;
}

.claim-form-section h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
}

.form-description {
  margin: 0 0 16px 0;
  color: var(--ion-color-medium);
  font-size: 14px;
}

.input-group {
  margin-bottom: 16px;
}

.custom-textarea {
  --border-radius: 12px;
  --padding-start: 16px;
  --padding-end: 16px;
  --background: var(--ion-color-light);
  margin-bottom: 8px;
}

.custom-textarea.textarea-filled {
  --background: var(--ion-color-light-tint);
}

.custom-textarea.textarea-error {
  --border-color: var(--ion-color-danger);
}

.custom-textarea:focus-within {
  --background: var(--ion-color-light-shade);
}

.error-message {
  color: var(--ion-color-danger);
  font-size: 12px;
  margin-top: 4px;
  padding-left: 16px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.verification-section {
  padding: 16px;
  background: var(--ion-color-light);
  border-radius: 12px;
}

.verification-section h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
}

.checklist {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.modal-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 24px;
}

.spinner {
  margin-right: 8px;
}

@media (max-width: 768px) {
  .items-grid {
    grid-template-columns: 1fr;
  }

  .items-container {
    padding: 12px;
  }

  .search-and-filter {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-button {
    align-self: flex-end;
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
    padding: 12px;
  }

  .modal-actions {
    margin-top: 16px;
  }
}

@media (min-width: 1200px) {
  .items-grid {
    grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  }
}
</style>
