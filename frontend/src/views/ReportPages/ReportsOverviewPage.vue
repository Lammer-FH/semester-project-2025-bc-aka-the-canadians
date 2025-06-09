<template>
  <template-page
    :headline="'Browse Reports'"
    addButtonPath="/items/report"
    addButtonText="Create New Report"
  >
    <template #header>
      <NavigationTabs v-model="activeTab" />
    </template>

    <div class="reports-container">
      <div class="search-and-filter">
        <ion-searchbar
          v-model="searchTerm"
          placeholder="Search for reports..."
          :debounce="300"
          class="custom-searchbar"
        ></ion-searchbar>

        <Filter
          modal-title="Filter Reports"
          :filter-configs="reportFilterConfigs"
          :filters="filters"
          @update-filter="updateFilter"
          @clear-filter="clearFilter"
          @apply-filters="applyFilters"
        />
      </div>

      <div class="stats-summary">
        <div class="stat-item">
          <ion-icon :icon="documentTextOutline" color="primary"></ion-icon>
          <span>{{ filteredReports.length }} Total Reports</span>
        </div>
        <div class="stat-item">
          <ion-icon :icon="checkmarkCircleOutline" color="success"></ion-icon>
          <span>{{ activeReportsCount }} Active</span>
        </div>
        <div class="stat-item">
          <ion-icon :icon="checkmarkCircleOutline" color="medium"></ion-icon>
          <span>{{ resolvedReportsCount }} Resolved</span>
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
        <ion-icon :icon="documentOutline" class="empty-icon"></ion-icon>
        <h2>No Reports Found</h2>
        <p v-if="activeFiltersCount > 0">
          No reports match the current filter criteria.
        </p>
        <p v-else>
          No reports created yet. Be the first to report a found or lost item!
        </p>
        <ion-button routerLink="/items/report" fill="solid">
          <ion-icon :icon="addOutline" slot="start"></ion-icon>
          Create First Report
        </ion-button>
      </div>

      <div v-else class="reports-grid">
        <ion-card
          v-for="report in filteredReports"
          :key="report.id"
          class="report-card"
          @click="navigateToReport(report.id)"
        >
          <div class="card-header">
            <div class="header-content">
              <h3 class="report-title">Report #{{ report.id }}</h3>
              <div class="status-chip" :class="getStatusClass(report.status)">
                <ion-icon
                  :icon="getStatusIcon(report.status)"
                  class="chip-icon"
                ></ion-icon>
                <span>{{ getStatusText(report.status) }}</span>
              </div>
            </div>
          </div>

          <ion-card-content class="card-content">
            <div class="report-details">
              <div class="detail-item">
                <ion-icon
                  :icon="locationOutline"
                  class="detail-icon"
                ></ion-icon>
                <span>{{ report.location?.name || "Unknown Location" }}</span>
              </div>
              <div class="detail-item">
                <ion-icon :icon="personOutline" class="detail-icon"></ion-icon>
                <span>{{ report.user?.name || "Anonymous" }}</span>
              </div>
              <div class="detail-item">
                <ion-icon :icon="timeOutline" class="detail-icon"></ion-icon>
                <span>{{ formatDate(report.createdAt) }}</span>
              </div>
            </div>

            <div
              v-if="report.items && report.items.length > 0"
              class="items-preview"
            >
              <h4>Items ({{ report.items.length }}):</h4>
              <div class="items-list">
                <span
                  v-for="(item, index) in report.items.slice(0, 3)"
                  :key="item.id"
                  class="item-tag"
                >
                  {{ item.name
                  }}{{
                    index < Math.min(report.items.length, 3) - 1 ? ", " : ""
                  }}
                </span>
                <span v-if="report.items.length > 3" class="more-items">
                  +{{ report.items.length - 3 }} more
                </span>
              </div>
            </div>

            <div class="metadata">
              <div class="metadata-item">
                <ion-icon
                  :icon="calendarOutline"
                  class="metadata-icon"
                ></ion-icon>
                <span class="metadata-text">{{
                  getTimeAgo(report.createdAt)
                }}</span>
              </div>
            </div>
          </ion-card-content>

          <div class="card-actions">
            <ion-button
              fill="clear"
              size="small"
              @click.stop="editReport(report.id)"
            >
              <ion-icon :icon="createOutline" slot="icon-only"></ion-icon>
            </ion-button>
            <ion-button
              fill="clear"
              size="small"
              color="primary"
              @click.stop="navigateToReport(report.id)"
            >
              View Details
            </ion-button>
          </div>
        </ion-card>
      </div>
    </div>
  </template-page>
</template>

<script setup lang="ts">
// filepath: frontend/src/views/ReportPages/ReportsOverviewPage.vue
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import {
  IonCard,
  IonCardContent,
  IonButton,
  IonIcon,
  IonSearchbar,
  IonSpinner,
} from "@ionic/vue";
import {
  flagOutline,
  locationOutline,
  documentTextOutline,
  checkmarkCircleOutline,
  alertCircleOutline,
  refreshOutline,
  documentOutline,
  addOutline,
  personOutline,
  timeOutline,
  calendarOutline,
  createOutline,
} from "ionicons/icons";
import { useReportStore } from "@/stores/reportStore";
import { ReportStatus } from "@/models/report";
const ReportStatusEnum = ReportStatus;
import TemplatePage from "@/components/TemplatePage.vue";
import NavigationTabs from "@/components/NavigationTabs.vue";
import Filter from "@/components/Filter.vue";

// Initialize store and router
const reportStore = useReportStore();
const router = useRouter();

const activeTab = ref("reports");
const searchTerm = ref("");

const filters = ref({
  status: null as ReportStatus | null,
  location: "" as string,
});

const reports = computed(() => reportStore.getReports);
const isLoading = computed(() => reportStore.isLoading);
const error = computed(() => reportStore.getError);

const uniqueLocations = computed(() => {
  const locations = reports.value
    .map(report => report.location?.name)
    .filter((location): location is string => Boolean(location));
  return [...new Set(locations)].sort();
});

const reportFilterConfigs = computed(() => [
  {
    key: "status",
    title: "Status",
    type: "radio" as const,
    icon: flagOutline,
    getLabel: (value: ReportStatus) => getStatusText(value),
    options: [
      { value: null, label: "All Reports" },
      { value: ReportStatusEnum.OPEN, label: "Open Reports" },
      { value: ReportStatusEnum.RESOLVED, label: "Resolved Reports" },
    ],
  },
  {
    key: "location",
    title: "Location",
    type: "select" as const,
    placeholder: "Select Location",
    icon: locationOutline,
    getLabel: (value: string) => value,
    options: [
      { value: "", label: "All Locations" },
      ...uniqueLocations.value.map(location => ({
        value: location,
        label: location,
      })),
    ],
  },
]);

const activeFiltersCount = computed(() => {
  return Object.values(filters.value).filter(value => {
    if (value === null || value === undefined || value === "") return false;
    return true;
  }).length;
});

const activeReportsCount = computed(
  () => reports.value.filter(report => report.status === ReportStatusEnum.OPEN).length
);

const resolvedReportsCount = computed(
  () => reports.value.filter(report => report.status === ReportStatusEnum.RESOLVED).length
);

const filteredReports = computed(() => {
  let filtered = reports.value;

  if (searchTerm.value.trim()) {
    const search = searchTerm.value.toLowerCase();
    filtered = filtered.filter(
      report =>
        report.id.toString().includes(search) ||
        report.user?.name?.toLowerCase().includes(search) ||
        report.location?.name?.toLowerCase().includes(search) ||
        report.items?.some(
          item =>
            item.name.toLowerCase().includes(search) ||
            item.description?.toLowerCase().includes(search)
        )
    );
  }

  if (filters.value.status !== null) {
    filtered = filtered.filter(
      report => report.status === filters.value.status
    );
  }

  if (filters.value.location) {
    filtered = filtered.filter(
      report => report.location?.name === filters.value.location
    );
  }

  return filtered;
});

const updateFilter = (key: string, value: ReportStatus | string | null): void => {
  if (key === "status") {
    filters.value.status = value as ReportStatus | null;
  } else if (key === "location") {
    filters.value.location = value as string;
  }
};

const clearFilter = (_key: string): void => {
  if (_key === "status") {
    filters.value.status = null;
  } else if (_key === "location") {
    filters.value.location = "";
  }
};

const applyFilters = (): void => {
  console.log("Filters applied");
};

// Methods
const getStatusText = (status: ReportStatus): string => {
  switch (status) {
    case ReportStatus.OPEN:
      return "Open";
    case ReportStatus.RESOLVED:
      return "Resolved";
    default:
      return "Unknown";
  }
};

const getStatusIcon = (status: ReportStatus): string => {
  switch (status) {
    case ReportStatus.OPEN:
      return documentTextOutline;
    case ReportStatus.RESOLVED:
      return checkmarkCircleOutline;
    default:
      return alertCircleOutline;
  }
};

const getStatusClass = (status: ReportStatus): string => {
  switch (status) {
    case ReportStatus.OPEN:
      return "status-open";
    case ReportStatus.RESOLVED:
      return "status-resolved";
    default:
      return "status-unknown";
  }
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

const navigateToReport = (reportId: number): void => {
  router.push(`/reports/${reportId}`);
};

const editReport = (reportId: number): void => {
  router.push(`/reports/${reportId}/edit`);
};

const loadReports = async (): Promise<void> => {
  try {
    await reportStore.fetchReports();
  } catch (error) {
    console.error("Error loading reports:", error);
  }
};

onMounted(async () => {
  await loadReports();
});
</script>

<style scoped>
.reports-container {
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

.reports-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 16px;
}

.report-card {
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.report-card:hover {
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

.report-title {
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

.status-chip.status-open {
  background: rgba(255, 193, 7, 0.2);
  color: #ff9800;
}

.status-chip.status-resolved {
  background: rgba(76, 175, 80, 0.2);
  color: #4caf50;
}

.status-chip.status-unknown {
  background: rgba(158, 158, 158, 0.2);
  color: #9e9e9e;
}

.chip-icon {
  font-size: 16px;
}

.card-content {
  padding: 16px;
}

.report-details {
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

.items-preview {
  margin-bottom: 16px;
}

.items-preview h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--ion-color-dark);
}

.items-list {
  font-size: 14px;
  color: var(--ion-color-medium);
}

.item-tag {
  font-weight: 500;
}

.more-items {
  font-style: italic;
  color: var(--ion-color-primary);
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

@media (max-width: 768px) {
  .reports-grid {
    grid-template-columns: 1fr;
  }

  .reports-container {
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
  .reports-grid {
    grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  }
}
</style>
