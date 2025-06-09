<template>
  <template-page
    :headline="'Browse Locations'"
    addButtonPath="/locations/add"
    addButtonText="Add New Location"
  >
    <template #header>
      <NavigationTabs v-model="activeTab" />
    </template>

    <div class="locations-container">
      <div class="search-section">
        <ion-searchbar
          v-model="searchTerm"
          placeholder="Search for locations..."
          :debounce="300"
          class="custom-searchbar"
        ></ion-searchbar>
      </div>

      <div class="reports-summary">
        <h3>Reports at Locations</h3>
        <div class="summary-stats">
          <div class="stat-item">
            <ion-icon :icon="locationOutline" color="primary"></ion-icon>
            <span>{{ filteredLocations.length }} Locations</span>
          </div>
          <div class="stat-item">
            <ion-icon :icon="eyeOutline" color="success"></ion-icon>
            <span>{{ totalOpenReports }} Open Reports</span>
          </div>
          <div class="stat-item">
            <ion-icon :icon="checkmarkOutline" color="primary"></ion-icon>
            <span>{{ totalResolvedReports }} Resolved Reports</span>
          </div>
          <div class="stat-item">
            <ion-icon :icon="documentTextOutline" color="tertiary"></ion-icon>
            <span>{{ totalReports }} Total Reports</span>
          </div>
        </div>
      </div>

      <div v-if="isLoading" class="loading-container">
        <ion-spinner name="crescent" color="primary"></ion-spinner>
        <p>Loading locations...</p>
      </div>

      <div v-else-if="error" class="empty-state">
        <ion-icon :icon="alertCircleOutline" class="empty-icon"></ion-icon>
        <h2>Error Loading</h2>
        <p>{{ error }}</p>
        <ion-button @click="loadLocations">
          <ion-icon :icon="refreshOutline" slot="start"></ion-icon>
          Try Again
        </ion-button>
      </div>

      <div v-else-if="filteredLocations.length === 0" class="empty-state">
        <ion-icon :icon="locationOutline" class="empty-icon"></ion-icon>
        <h2>No Locations Found</h2>
        <p v-if="searchTerm">
          No locations match the search term "{{ searchTerm }}".
        </p>
        <p v-else>No locations created yet. Add the first location!</p>
        <ion-button @click="navigateToAddLocation">
          <ion-icon :icon="addOutline" slot="start"></ion-icon>
          Add First Location
        </ion-button>
      </div>

      <div v-else class="locations-grid">
        <UniversalCard
          v-for="(location, index) in filteredLocations"
          :key="location.id"
          :title="location.name"
          :description="location.description"
          :content-sections="getLocationContentSections(location)"
          :metadata="getLocationMetadata(location)"
          :actions="getLocationActions(location)"
          card-type="location"
          :animation-delay="index * 0.1"
          @card-click="navigateToLocation(location.id)"
          @list-item-click="handleLocationListClick"
        />
      </div>
    </div>
  </template-page>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { IonSearchbar, IonButton, IonIcon, IonSpinner } from "@ionic/vue";
import {
  locationOutline,
  addOutline,
  refreshOutline,
  eyeOutline,
  createOutline,
  alertCircleOutline,
  documentTextOutline,
  calendarOutline,
  flagOutline,
  checkmarkCircleOutline,
  checkmarkOutline,
} from "ionicons/icons";
import { useLocationStore } from "@/stores/locationStore";
import { useReportStore } from "@/stores/reportStore";
import { ReportStatus } from "@/models/report";
import TemplatePage from "@/components/TemplatePage.vue";
import NavigationTabs from "@/components/NavigationTabs.vue";
import UniversalCard from "@/components/OverviewCard.vue";
import type { Location } from "@/models/location";
import type { Report } from "@/models/report";

const router = useRouter();
const locationStore = useLocationStore();
const reportStore = useReportStore();
const activeTab = ref("locations");
const searchTerm = ref("");

const locations = computed(() => locationStore.getLocations || []);
const isLoading = computed(
  () => locationStore.isLoading || reportStore.isLoading
);
const error = computed(() => locationStore.getError);

const reports = computed(() => reportStore.getReports || []);

onMounted(async () => {
  await loadLocations();
  await loadReports();
});

const loadLocations = async () => {
  try {
    await locationStore.fetchLocations();
  } catch (error) {
    console.error("Error loading locations:", error);
  }
};

const loadReports = async () => {
  try {
    await reportStore.fetchReports();
  } catch (err) {
    console.error("Error loading reports:", err);
  }
};

const filteredLocations = computed(() => {
  const locationsArray = locations.value;
  if (!Array.isArray(locationsArray)) {
    return [];
  }

  return locationsArray.filter((location: Location) =>
    location.name.toLowerCase().includes(searchTerm.value.toLowerCase())
  );
});

const totalOpenReports = computed(() => {
  return reports.value.filter(report => report.status === ReportStatus.OPEN).length;
});

const totalResolvedReports = computed(() => {
  return reports.value.filter(report => report.status === ReportStatus.RESOLVED).length;
});

const totalReports = computed(() => {
  return reportStore.getReports.length;
});

const getReportsForLocation = (location: Location): Report[] => {
  return reports.value.filter(report => report.locationId === location.id);
};

const getOpenReportsCount = (location: Location): number => {
  return getReportsForLocation(location).filter(
    report => report.status === ReportStatus.OPEN
  ).length;
};

const getResolvedReportsCount = (location: Location): number => {
  return getReportsForLocation(location).filter(
    report => report.status === ReportStatus.RESOLVED
  ).length;
};

const getTotalReportsCount = (location: Location): number => {
  return getReportsForLocation(location).length;
};

const getRecentReportsForLocation = (location: Location): Report[] => {
  return getReportsForLocation(location)
    .sort(
      (a, b) =>
        new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
    )
    .slice(0, 3);
};

const getStatusColor = (status: ReportStatus) => {
  return status === ReportStatus.RESOLVED ? "success" : "warning";
};

const getStatusIcon = (status: ReportStatus) => {
  return status === ReportStatus.RESOLVED ? checkmarkCircleOutline : alertCircleOutline;
};

const getTimeAgo = (dateString: string) => {
  const date = new Date(dateString);
  const now = new Date();
  const diffInHours = Math.floor(
    (now.getTime() - date.getTime()) / (1000 * 60 * 60)
  );

  if (diffInHours < 1) return "A few minutes ago";
  if (diffInHours < 24) return `${diffInHours} hours ago`;

  const diffInDays = Math.floor(diffInHours / 24);
  if (diffInDays < 7) return `${diffInDays} days ago`;

  return date.toLocaleDateString("en-US");
};

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString("en-US", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const navigateToLocation = (locationId: number) => {
  router.push(`/locations/${locationId}`);
};

const editLocation = (locationId: number) => {
  router.push(`/locations/${locationId}/edit`);
};

const navigateToAddLocation = () => {
  router.push("/locations/add");
};

const viewAllReportsAtLocation = (locationName: string) => {
  router.push({
    path: "/items/overview",
    query: { location: locationName },
  });
};

const handleLocationListClick = (item: { data?: Report; title: string; subtitle?: string }, sectionKey: string) => {
  if (sectionKey === "recentReports" && item.data) {
    router.push(`/reports/${item.data.id}`);
  }
};

const getLocationContentSections = (location: Location) => {
  const sections = [];

  const statsData = [];
  const openCount = getOpenReportsCount(location);
  const resolvedCount = getResolvedReportsCount(location);
  const totalCount = getTotalReportsCount(location);

  if (openCount > 0) {
    statsData.push({
      key: "open",
      icon: alertCircleOutline,
      color: "warning",
      value: openCount,
      label: "Open",
    });
  }

  if (resolvedCount > 0) {
    statsData.push({
      key: "resolved",
      icon: checkmarkOutline,
      color: "success",
      value: resolvedCount,
      label: "Resolved",
    });
  }

  statsData.push({
    key: "total",
    icon: documentTextOutline,
    color: "primary",
    value: totalCount,
    label: "Total",
  });

  sections.push({
    key: "reports",
    title: "Reports at this Location",
    icon: flagOutline,
    type: "stats" as const,
    data: statsData,
  });

  const recentReports = getRecentReportsForLocation(location);
  if (recentReports.length > 0) {
    sections.push({
      key: "recentReports",
      title: "Latest Reports",
      type: "list" as const,
      maxItems: 2,
      data: recentReports.slice(0, 2).map(report => ({
        title: report.items?.[0]?.name || `Report #${report.id}`,
        subtitle: getTimeAgo(report.createdAt),
        icon: getStatusIcon(report.status),
        color: getStatusColor(report.status),
        data: report,
      })),
    });
  }

  return sections;
};

const getLocationMetadata = (location: Location) => [
  {
    key: "created",
    icon: calendarOutline,
    value: `Created ${formatDate(location.createdAt)}`,
  },
];

const getLocationActions = (location: Location) => [
  {
    key: "view",
    label: "View Location",
    icon: eyeOutline,
    fill: "clear" as const,
    handler: () => navigateToLocation(location.id),
  },
  {
    key: "reports",
    label: `Reports (${getReportsForLocation(location).length})`,
    icon: flagOutline,
    fill: "clear" as const,
    handler: () => viewAllReportsAtLocation(location.name),
  },
  {
    key: "edit",
    label: "Edit",
    icon: createOutline,
    fill: "clear" as const,
    handler: () => editLocation(location.id),
  },
];
</script>

<style scoped>
.locations-container {
  padding: 16px;
  min-height: 100vh;
}

.search-section {
  margin-bottom: 20px;
}

.custom-searchbar {
  --background: var(--ion-color-light);
  --border-radius: 12px;
  --box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.reports-summary {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.reports-summary h3 {
  color: var(--ion-color-dark);
  margin: 0 0 16px 0;
  font-size: 1.1em;
  font-weight: 600;
}

.summary-stats {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  gap: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.9em;
  font-weight: 500;
  background: var(--ion-color-light-tint);
  padding: 8px 12px;
  border-radius: 8px;
  flex: 1;
  min-width: 120px;
  justify-content: center;
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

.locations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 16px;
  animation: fadeInUp 0.6s ease-out;
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

@media (max-width: 768px) {
  .locations-grid {
    grid-template-columns: 1fr;
  }

  .locations-container {
    padding: 12px;
  }

  .summary-stats {
    flex-direction: column;
  }

  .stat-item {
    justify-content: flex-start;
  }
}

@media (min-width: 1200px) {
  .locations-grid {
    grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  }
}
</style>
