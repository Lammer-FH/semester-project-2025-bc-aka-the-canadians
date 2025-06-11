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
        <UniversalCard
          v-for="(item, index) in filteredItems"
          :key="item.id"
          :title="item.name"
          :description="item.description"
          :status="getItemStatus(item)"
          :details="getItemDetails(item)"
          :metadata="getItemMetadata(item)"
          card-type="item"
          :animation-delay="index * 0.1"
          @card-click="navigateToItem(item.id)"
        />
      </div>
    </div>
  </template-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { IonButton, IonIcon, IonSearchbar, IonSpinner } from "@ionic/vue";
import {
  personOutline,
  cubeOutline,
  checkmarkCircleOutline,
  searchOutline,
  alertCircleOutline,
  refreshOutline,
  addOutline,
  timeOutline,
  calendarOutline,
} from "ionicons/icons";
import { useItemStore } from "@/stores/itemStore";
import { Item, ItemStatus } from "@/models/item";
import { ReportStatus } from "@/models/report";
import TemplatePage from "@/components/TemplatePage.vue";
import NavigationTabs from "@/components/NavigationTabs.vue";
import Filter from "@/components/Filter.vue";
import UniversalCard from "@/components/OverviewCard.vue";

const router = useRouter();
const itemStore = useItemStore();

const activeTab = ref("items");
const searchTerm = ref("");

const filters = ref({
  claimedStatus: null as ItemStatus | null,
});

const itemFilterConfigs = computed(() => [
  {
    key: "claimedStatus",
    title: "Claim Status",
    type: "radio" as const,
    icon: personOutline,
    getLabel: (value: ItemStatus) => (value === ItemStatus.CLAIMED ? "Claimed" : "Unclaimed"),
    options: [
      { value: null, label: "All Items" },
      { value: ItemStatus.UNCLAIMED, label: "Unclaimed Items" },
      { value: ItemStatus.CLAIMED, label: "Claimed Items" },
    ],
  },
]);

const items = computed(() => itemStore.getItems);
const isLoading = computed(() => itemStore.isLoading);
const error = computed(() => itemStore.getError);

const activeFiltersCount = computed(() => {
  return Object.entries(filters.value).filter(([value]) => {
    if (value === null || value === undefined) return false;
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
    filtered = filtered.filter(item => item.status === filters.value.claimedStatus);
  }

  return filtered;
});

const claimedItemsCount = computed(
  () => items.value.filter(item => item.status === ItemStatus.CLAIMED).length
);

const unclaimedItemsCount = computed(
  () => items.value.filter(item => item.status === ItemStatus.UNCLAIMED).length
);

const getItemStatus = (item: Item): string => {
  if (item.status === ItemStatus.CLAIMED) {
    return 'claimed';
  } else {
    return "unclaimed";
  }
};

const getItemDetails = (item: Item) => [
  {
    key: "created",
    icon: timeOutline,
    value: formatDate(item.createdAt),
  },
];

const getItemMetadata = (item: Item) => [
  {
    key: "timeAgo",
    icon: calendarOutline,
    value: getTimeAgo(item.createdAt),
  },
];

const updateFilter = (key: string, value: any): void => {
  filters.value[key as keyof typeof filters.value] = value;
};

const clearFilter = (): void => {
  filters.value.claimedStatus = null;
};

const applyFilters = (): void => {
  console.log("Filters applied");
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

const navigateToItem = (itemId: number): void => {
  router.push(`/items/${itemId}`);
};

const loadItems = async (): Promise<void> => {
  try {
    await itemStore.fetchItems();
  } catch (error) {
    console.error("Error loading items:", error);
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
}

@media (min-width: 1200px) {
  .items-grid {
    grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  }
}
</style>
