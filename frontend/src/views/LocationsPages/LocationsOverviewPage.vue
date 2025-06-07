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
        <ion-card
          v-for="(location, index) in filteredLocations"
          :key="location.id"
          class="location-card"
          :style="{ '--animation-delay': `${index * 0.1}s` }"
          @click="navigateToLocation(location.id)"
        >
          <ion-card-header class="card-header">
            <div class="header-content">
              <ion-card-title class="location-name">{{
                location.name
              }}</ion-card-title>
            </div>
          </ion-card-header>

          <ion-card-content class="card-content">
            <div class="reports-at-location">
              <h4>
                <ion-icon :icon="flagOutline" class="section-icon"></ion-icon>
                Reports at this Location
              </h4>

              <div
                v-if="getReportsForLocation(location).length === 0"
                class="no-reports"
              >
                <ion-icon
                  :icon="checkmarkCircleOutline"
                  class="no-reports-icon"
                ></ion-icon>
                <span>No active reports</span>
              </div>

              <div v-else class="reports-stats">
                <div
                  class="report-stat-item"
                  v-if="getOpenReportsCount(location) > 0"
                >
                  <ion-chip color="warning" class="report-chip">
                    <ion-icon :icon="alertCircleOutline" class="chip-icon"></ion-icon>
                    {{ getOpenReportsCount(location) }} Open
                  </ion-chip>
                </div>
                <div
                  class="report-stat-item"
                  v-if="getResolvedReportsCount(location) > 0"
                >
                  <ion-chip color="success" class="report-chip">
                    <ion-icon
                      :icon="checkmarkOutline"
                      class="chip-icon"
                    ></ion-icon>
                    {{ getResolvedReportsCount(location) }} Resolved
                  </ion-chip>
                </div>
                <div
                  class="report-stat-item"
                  v-if="getTotalReportsCount(location) > 0"
                >
                  <ion-chip color="primary" class="report-chip">
                    <ion-icon
                      :icon="documentTextOutline"
                      class="chip-icon"
                    ></ion-icon>
                    {{ getTotalReportsCount(location) }} Total
                  </ion-chip>
                </div>
              </div>

              <div
                v-if="getRecentReportsForLocation(location).length > 0"
                class="recent-reports"
              >
                <div class="recent-reports-header">
                  <span>Latest Reports:</span>
                  <ion-button
                    fill="clear"
                    size="small"
                    @click.stop="viewAllReportsAtLocation(location.name)"
                  >
                    View All
                  </ion-button>
                </div>
                <div class="recent-reports-list">
                  <div
                    v-for="report in getRecentReportsForLocation(
                      location
                    ).slice(0, 2)"
                    :key="report.id"
                    class="recent-report-item"
                    @click.stop="navigateToReport(report.id)"
                  >
                    <div class="report-info">
                      <ion-icon
                        :icon="getStatusIcon(report.status)"
                        :color="getStatusColor(report.status)"
                        class="report-icon"
                      ></ion-icon>
                      <div class="report-text">
                        <span class="report-title">{{ report.title }}</span>
                        <span class="report-time">{{
                          getTimeAgo(report.dateCreated)
                        }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="location.description" class="description">
              <h4>
                <ion-icon
                  :icon="documentTextOutline"
                  class="section-icon"
                ></ion-icon>
                Description
              </h4>
              <p>{{ location.description }}</p>
            </div>

            <div class="metadata">
              <div class="metadata-item">
                <ion-icon
                  :icon="calendarOutline"
                  class="metadata-icon"
                ></ion-icon>
                <span class="metadata-text"
                  >Created {{ formatDate(location.createdAt) }}</span
                >
              </div>
            </div>
          </ion-card-content>

          <div class="card-actions">
            <ion-button
              fill="clear"
              size="small"
              @click.stop="navigateToLocation(location.id)"
            >
              <ion-icon :icon="eyeOutline" slot="start"></ion-icon>
              View Location
            </ion-button>
            <ion-button
              fill="clear"
              size="small"
              @click.stop="viewAllReportsAtLocation(location.name)"
            >
              <ion-icon :icon="flagOutline" slot="start"></ion-icon>
              Reports ({{ getReportsForLocation(location).length }})
            </ion-button>
            <ion-button
              fill="clear"
              size="small"
              @click.stop="editLocation(location.id)"
            >
              <ion-icon :icon="createOutline" slot="start"></ion-icon>
              Edit
            </ion-button>
          </div>
        </ion-card>
      </div>
    </div>
  </template-page>
</template>

<script setup lang="ts">
import TemplatePage from "@/components/TemplatePage.vue";
import NavigationTabs from "@/components/NavigationTabs.vue";
import {
  IonSearchbar,
  IonCard,
  IonCardHeader,
  IonCardTitle,
  IonCardContent,
  IonButton,
  IonIcon,
  IonChip,
  IonSpinner,
} from "@ionic/vue";
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
  searchOutline,
  checkmarkOutline,
} from "ionicons/icons";
import { ref, computed, watch, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useLocationStore } from "@/stores/locationStore";
import { useReportStore } from "@/stores/reportStore";
import type { Location } from "@/models/location";
import type { Report } from "@/models/report";

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
const locationStore = useLocationStore();
const reportStore = useReportStore();
const activeTab = ref("locations");
const searchTerm = ref("");

const locations = computed(() => locationStore.getLocations || []);
const isLoading = computed(
  () => locationStore.isLoading || reportStore.isLoading
);
const error = computed(() => locationStore.getError);

const reports = computed((): VirtualReport[] => {
  return reportStore.getReports.map((report: Report) => ({
    id: report.id,
    title: report.items?.[0]?.name || "Unknown Item",
    description: report.items?.[0]?.description || "No description available",
    status: report.status ? "RESOLVED" : "OPEN",
    location: report.location?.name || "Unknown Location",
    dateCreated: report.createdAt,
    reporterName: report.user?.name || "Unknown Reporter",
  }));
});

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
  return reports.value.filter(report => report.status.toUpperCase() === "OPEN")
    .length;
});

const totalResolvedReports = computed(() => {
  return reports.value.filter(report => report.status.toUpperCase() === "RESOLVED")
    .length;
});

const totalReports = computed(() => {
  return reports.value.length;
});

const getReportsForLocation = (location: Location): VirtualReport[] => {
  return reports.value.filter(
    report => reportStore.getReports.find(r => r.id === report.id)?.locationId === location.id
  );
};

const getOpenReportsCount = (location: Location): number => {
  return getReportsForLocation(location).filter(
    report => report.status.toUpperCase() === "OPEN"
  ).length;
};

const getResolvedReportsCount = (location: Location): number => {
  return getReportsForLocation(location).filter(
    report => report.status.toUpperCase() === "RESOLVED"
  ).length;
};

const getTotalReportsCount = (location: Location): number => {
  return getReportsForLocation(location).length;
};

const getRecentReportsForLocation = (location: Location): VirtualReport[] => {
  return getReportsForLocation(location)
    .sort(
      (a, b) =>
        new Date(b.dateCreated).getTime() - new Date(a.dateCreated).getTime()
    )
    .slice(0, 3);
};

const getStatusColor = (status: string) => {
  switch (status.toUpperCase()) {
    case "FOUND":
      return "success";
    case "LOST":
      return "warning";
    case "CLAIMED":
      return "medium";
    case "RETURNED":
      return "success";
    default:
      return "primary";
  }
};

const getStatusIcon = (status: string) => {
  switch (status.toUpperCase()) {
    case "FOUND":
      return eyeOutline;
    case "LOST":
      return searchOutline;
    case "CLAIMED":
      return checkmarkOutline;
    case "RETURNED":
      return checkmarkCircleOutline;
    default:
      return flagOutline;
  }
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

const navigateToReport = (reportId: number) => {
  router.push(`/items/${reportId}`);
};

const viewAllReportsAtLocation = (locationName: string) => {
  router.push({
    path: "/items/overview",
    query: { location: locationName },
  });
};

watch(activeTab, tab => {
  if (tab === "items") {
    router.push("/items/overview");
  }
});
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

.location-card {
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

.location-card:hover {
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

.card-header::before {
  content: "";
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

.location-card:hover .card-header::before {
  transform: rotate(45deg) translateX(100%);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.location-name {
  color: white;
  font-size: 1.2em;
  font-weight: 600;
  margin: 0;
  flex: 1;
  margin-right: 12px;
}

.building-chip {
  --background: rgba(255, 255, 255, 0.2);
  --color: white;
  margin: 0;
}

.chip-icon {
  font-size: 14px;
  margin-right: 4px;
}

.location-details {
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

.reports-at-location {
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--ion-color-light-shade);
}

.reports-at-location h4 {
  color: var(--ion-color-dark);
  margin: 0 0 12px 0;
  font-size: 1em;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-icon {
  font-size: 16px;
  color: var(--ion-color-primary);
}

.no-reports {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--ion-color-medium);
  font-size: 0.9em;
  padding: 8px 12px;
  background: var(--ion-color-light-tint);
  border-radius: 8px;
}

.no-reports-icon {
  font-size: 16px;
}

.reports-stats {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.report-chip {
  --background: rgba(var(--ion-color-rgb), 0.1);
  font-size: 0.8em;
}

.recent-reports {
  margin-top: 12px;
}

.recent-reports-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 0.9em;
  color: var(--ion-color-medium);
  font-weight: 500;
}

.recent-reports-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.recent-report-item {
  display: flex;
  align-items: center;
  padding: 8px;
  background: var(--ion-color-light-tint);
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s ease;
}

.recent-report-item:hover {
  background: var(--ion-color-light-shade);
}

.report-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.report-icon {
  font-size: 14px;
}

.report-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.report-title {
  font-size: 0.85em;
  font-weight: 500;
  color: var(--ion-color-dark);
}

.report-time {
  font-size: 0.75em;
  color: var(--ion-color-medium);
}

.description {
  margin-bottom: 16px;
}

.description h4 {
  color: var(--ion-color-dark);
  margin: 0 0 8px 0;
  font-size: 1em;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.description p {
  color: var(--ion-color-medium-shade);
  line-height: 1.4;
  margin: 0;
  font-size: 0.9em;
  display: -webkit-box;
  -webkit-line-clamp: 2;
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

  .card-actions {
    flex-direction: column;
    gap: 8px;
  }

  .header-content {
    flex-direction: column;
    gap: 8px;
  }

  .building-chip {
    align-self: flex-start;
  }
}

@media (min-width: 1200px) {
  .locations-grid {
    grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  }
}
</style>
