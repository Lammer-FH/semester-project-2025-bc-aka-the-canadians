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
          <span>{{ openReportsCount }} Open</span>
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
        <ion-button @click="loadReports" class="btn-secondary-action">
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
        <ion-button routerLink="/items/report" class="btn-primary-action">
          <ion-icon :icon="addOutline" slot="start"></ion-icon>
          Create First Report
        </ion-button>
      </div>

      <div v-else class="reports-grid">
        <UniversalCard
          v-for="(report, index) in filteredReports"
          :key="report.id"
          :title="`Report #${report.id}`"
          :status="getStatusText(report.status)"
          :content-sections="getReportContentSections(report)"
          :details="getReportDetails(report)"
          :metadata="getReportMetadata(report)"
          :actions="getReportActions(report)"
          card-type="report"
          :animation-delay="index * 0.1"
          @card-click="navigateToReport(report.id)"
          @list-item-click="handleReportListClick"
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
import UniversalCard from "@/components/OverviewCard.vue";
import type { Report } from "@/models/report";
import type { Item } from "@/models/item";

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
  () =>
    reports.value.filter(report => report.status === ReportStatusEnum.OPEN)
      .length
);

const resolvedReportsCount = computed(
  () =>
    reports.value.filter(report => report.status === ReportStatusEnum.RESOLVED)
      .length
);

const filteredReports = computed(() => {
  let filtered = reports.value;

  if (searchTerm.value.trim()) {
    const search = searchTerm.value.toLowerCase();
    filtered = filtered.filter(report =>
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

const getReportContentSections = (report: Report) => {
  const sections = [];

  if (report.items && report.items.length > 0) {
    sections.push({
      key: "items",
      title: `Items (${report.items.length})`,
      type: "list" as const,
      maxItems: 3,
      data: report.items.map(item => ({
        title: item.name,
        subtitle: item.description || undefined,
        data: item,
      })),
    });
  }

  return sections;
};

const getReportDetails = (report: Report) => [
  {
    key: "location",
    icon: locationOutline,
    value: report.location?.name || "Unknown Location",
  },
  {
    key: "reporter",
    icon: personOutline,
    value: report.user?.name || "Anonymous",
  },
  {
    key: "created",
    icon: timeOutline,
    value: formatDate(report.createdAt),
  },
];

const getReportMetadata = (report: Report) => [
  {
    key: "timeAgo",
    icon: calendarOutline,
    value: getTimeAgo(report.createdAt),
  },
];

const getReportActions = (report: Report) => [
  {
    key: "edit",
    label: "",
    icon: createOutline,
    iconSlot: "icon-only" as const,
    fill: "clear" as const,
    size: "small" as const,
    handler: () => editReport(report.id),
  },
  {
    key: "view",
    label: "View Details",
    fill: "clear" as const,
    color: "primary",
    size: "small" as const,
    handler: () => navigateToReport(report.id),
  },
];

const handleReportListClick = (
  item: { data?: Item; title: string; subtitle?: string },
  sectionKey: string
) => {
  if (sectionKey === "items" && item.data) {
    router.push(`/items/${item.data.id}`);
  }
};

const updateFilter = (
  key: string,
  value: ReportStatus | string | null
): void => {
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

.btn-primary-action {
  --background: var(--ion-color-primary);
  --color: white;
  --border-radius: 12px;
  font-weight: 600;
  height: 48px;
}

.btn-secondary-action {
  --background: transparent;
  --color: var(--ion-color-primary);
  --border-color: var(--ion-color-primary);
  --border-width: 2px;
  --border-style: solid;
  --border-radius: 12px;
  font-weight: 600;
  height: 48px;
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
}

@media (min-width: 1200px) {
  .reports-grid {
    grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  }
}
</style>
