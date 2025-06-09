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

        <Filter
          modal-title="Filter Items"
          :filter-configs="itemFilterConfigs"
          :filters="filters"
          @update-filter="updateFilter"
          @clear-filter="clearFilter"
          @apply-filters="applyFilters"
        />
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
          <div class="card-header">
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
  IonSpinner,
  IonModal,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonTextarea,
  IonCheckbox,
} from "@ionic/vue";
import {
  personOutline,
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
  closeOutline,
} from "ionicons/icons";
import { useItemStore } from "@/stores/itemStore";
import { Item } from "@/models/item";
import TemplatePage from "@/components/TemplatePage.vue";
import NavigationTabs from "@/components/NavigationTabs.vue";
import Filter from "@/components/Filter.vue";

const router = useRouter();
const itemStore = useItemStore();

const activeTab = ref("items");
const searchTerm = ref("");

const filters = ref({
  claimedStatus: null as boolean | null,
});

const itemFilterConfigs = computed(() => [
  {
    key: "claimedStatus",
    title: "Claim Status",
    type: "radio" as const,
    icon: personOutline,
    getLabel: (value: boolean) => (value ? "Claimed" : "Unclaimed"),
    options: [
      { value: null, label: "All Items" },
      { value: false, label: "Unclaimed Items" },
      { value: true, label: "Claimed Items" },
    ],
  },
]);

const items = computed(() => itemStore.getItems);
const isLoading = computed(() => itemStore.isLoading);
const error = computed(() => itemStore.getError);

const activeFiltersCount = computed(() => {
  return Object.entries(filters.value).filter(([key, value]) => {
    if (value === null || value === undefined || value === "") return false;
    return true;
  }).length;
});

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

  if (filters.value.claimedStatus !== null) {
    if (filters.value.claimedStatus) {
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

const updateFilter = (key: string, value: any): void => {
  filters.value[key as keyof typeof filters.value] = value;
};

const clearFilter = (key: string): void => {
  filters.value[key as keyof typeof filters.value] =
    key === "claimedStatus" ? null : "";
};

const applyFilters = (): void => {
  console.log("Filters applied");
};

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
  color: var(--ion-color-medium);
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
  gap: 8px;
}

/* Claim Modal Styles */
.claim-modal-content {
  padding: 16px;
}

.claim-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.item-info-section h2 {
  margin: 0 0 12px 0;
  color: var(--ion-color-dark);
  font-weight: 600;
}

.item-description {
  margin: 0 0 16px 0;
  color: var(--ion-color-medium);
  font-size: 14px;
  line-height: 1.4;
}

.item-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-row {
  font-size: 14px;
  color: var(--ion-color-medium);
}

.claim-form-section h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--ion-color-dark);
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
  --background: var(--ion-color-light-tint);
  --border-radius: 12px;
  border: 2px solid var(--ion-color-light-shade);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.custom-textarea.textarea-filled {
  border-color: var(--ion-color-primary-tint);
  --background: rgba(var(--ion-color-primary-rgb), 0.05);
}

.custom-textarea.textarea-error {
  border-color: var(--ion-color-danger);
  --background: rgba(var(--ion-color-danger-rgb), 0.05);
}

.error-message {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--ion-color-danger);
  font-size: 0.85em;
  margin-top: 4px;
  padding-left: 4px;
}

.verification-section h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--ion-color-dark);
}

.checklist {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.modal-actions {
  display: flex;
  gap: 12px;
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

  .stats-summary {
    flex-direction: column;
    gap: 12px;
  }

  .card-actions {
    flex-direction: column;
    gap: 8px;
  }
}

@media (min-width: 1200px) {
  .items-grid {
    grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  }
}
</style>
