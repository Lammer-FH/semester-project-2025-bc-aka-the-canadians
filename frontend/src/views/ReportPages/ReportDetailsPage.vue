<template>
  <template-page
    :headline="report?.id ? `Report #${report.id}` : 'Report Details'"
    :leftFooterButton="leftFooterButton"
    :rightFooterButton="rightFooterButton"
    @leftFooterButtonClicked="handleBack"
    @rightFooterButtonClicked="editReport"
  >
    <div class="report-details-container">
      <div v-if="isLoading" class="loading-container">
        <ion-spinner name="crescent" color="primary"></ion-spinner>
        <p>Loading report details...</p>
      </div>

      <div v-else-if="error" class="error-container">
        <ion-icon :icon="alertCircleOutline" class="error-icon"></ion-icon>
        <h2>Error Loading Report</h2>
        <p>{{ error }}</p>
        <ion-button @click="loadReport" fill="outline">
          <ion-icon :icon="refreshOutline" slot="start"></ion-icon>
          Try Again
        </ion-button>
      </div>

      <div v-else-if="report" class="report-content">
        <!-- Report Information -->
        <ion-card class="info-card">
          <ion-card-header>
            <ion-card-title>
              <ion-icon
                :icon="informationCircleOutline"
                class="section-icon"
              ></ion-icon>
              Report Information
            </ion-card-title>
          </ion-card-header>
          <ion-card-content>
            <div class="info-grid">
              <div class="info-item">
                <label>Report ID</label>
                <span>#{{ report.id }}</span>
              </div>
              <div class="info-item">
                <label>Status</label>
                <span
                  class="status-text"
                  :class="getStatusClass(report.status)"
                >
                  {{ getStatusText(report.status) }}
                </span>
              </div>
              <div class="info-item">
                <label>Created</label>
                <span>{{ formatDate(report.createdAt) }}</span>
              </div>
              <div class="info-item">
                <label>Reporter</label>
                <span>{{ report.user?.name || "Anonymous" }}</span>
              </div>
              <div class="info-item">
                <label>Location</label>
                <span>{{ report.location?.name || "Unknown Location" }}</span>
              </div>
              <div class="info-item">
                <label>Time Ago</label>
                <span>{{ getTimeAgo(report.createdAt) }}</span>
              </div>
            </div>
          </ion-card-content>
        </ion-card>

        <!-- Items List -->
        <ion-card class="items-card">
          <ion-card-header>
            <ion-card-title>
              <ion-icon :icon="cubeOutline" class="section-icon"></ion-icon>
              Item
            </ion-card-title>
          </ion-card-header>
          <ion-card-content>
            <div
              v-if="report.items && report.items.length > 0"
              class="items-list"
            >
              <div
                v-for="item in report.items"
                :key="item.id"
                class="item-card"
              >
                <div class="item-header">
                  <h3>{{ item.name }}</h3>
                </div>
                <p v-if="item.description" class="item-description">
                  {{ truncateDescription(item.description, 100) }}
                </p>
                <div class="item-meta">
                  <span class="meta-item">
                    <ion-icon :icon="timeOutline" class="meta-icon"></ion-icon>
                    {{ formatDate(item.createdAt) }}
                  </span>
                  <span v-if="item.claimedByUser" class="meta-item">
                    <ion-icon
                      :icon="personOutline"
                      class="meta-icon"
                    ></ion-icon>
                    Claimed by {{ item.claimedByUser.name }}
                  </span>
                </div>
                <div class="item-actions">
                  <ion-button
                    fill="clear"
                    size="small"
                    @click.stop="navigateToItem(item.id)"
                  >
                    View Details
                  </ion-button>
                </div>
              </div>
            </div>
            <div v-else class="empty-items">
              <ion-icon :icon="cubeOutline" class="empty-icon"></ion-icon>
              <h3>No Items</h3>
              <p>No items have been added to this report yet.</p>
            </div>
          </ion-card-content>
        </ion-card>
      </div>
    </div>
  </template-page>
</template>

<script setup lang="ts">
import { computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import TemplatePage from "@/components/TemplatePage.vue";
import {
  IonCard,
  IonCardHeader,
  IonCardTitle,
  IonCardContent,
  IonButton,
  IonIcon,
  IonSpinner,
} from "@ionic/vue";
import {
  alertCircleOutline,
  refreshOutline,
  informationCircleOutline,
  createOutline,
  cubeOutline,
  timeOutline,
  personOutline,
  arrowBackOutline,
} from "ionicons/icons";
import { useReportStore } from "@/stores/reportStore";
import { ReportStatus } from "@/models/report";
const ReportStatusEnum = ReportStatus;

const router = useRouter();
const route = useRoute();
const reportStore = useReportStore();

// Computed properties
const report = computed(() => reportStore.getCurrentReport);
const isLoading = computed(() => reportStore.isLoading);
const error = computed(() => reportStore.getError);
const leftFooterButton = computed(() => ({
  name: "Back",
  color: "medium",
  icon: arrowBackOutline,
}));
const rightFooterButton = computed(() => ({
  name: "Edit",
  color: "primary",
  icon: createOutline,
}));

// Methods
const getStatusText = (status: ReportStatus): string => {
  switch (status) {
    case ReportStatusEnum.OPEN:
      return "Open";
    case ReportStatusEnum.RESOLVED:
      return "Resolved";
    default:
      return "Unknown";
  }
};

const getStatusClass = (status: ReportStatus): string => {
  switch (status) {
    case ReportStatusEnum.OPEN:
      return "status-open";
    case ReportStatusEnum.RESOLVED:
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
    month: "long",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const truncateDescription = (
  description: string,
  maxLength: number = 100
): string => {
  if (description.length <= maxLength) return description;
  return description.substring(0, maxLength) + "...";
};

const loadReport = async (): Promise<void> => {
  const reportId = parseInt(route.params.id as string);
  if (reportId) {
    await reportStore.fetchReportById(reportId);
  }
};

const editReport = (): void => {
  router.push(`/reports/${route.params.id}/edit`);
};

const navigateToItem = (itemId: number): void => {
  router.push(`/items/${itemId}`);
};

// Lifecycle
onMounted(async () => {
  await loadReport();
});

function handleBack() {
  router.back();
}
</script>

<style scoped>
.report-details-container {
  padding: 16px;
  max-width: 1000px;
  margin: 0 auto;
}

.loading-container,
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 48px 16px;
  text-align: center;
}

.error-icon {
  font-size: 64px;
  color: var(--ion-color-danger);
}

.error-container h2 {
  margin: 0;
  color: var(--ion-color-danger);
}

.error-container p {
  margin: 0;
  color: var(--ion-color-medium);
}

.report-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px;
  background: var(--ion-color-light);
  border-radius: 12px;
  margin-bottom: 8px;
}

.header-info h1 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: var(--ion-color-dark);
}

.status-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 500;
}

.chip-icon {
  font-size: 16px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.info-card,
.items-card {
  margin: 0;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-icon {
  margin-right: 8px;
  font-size: 20px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item label {
  font-size: 12px;
  font-weight: 600;
  color: var(--ion-color-medium);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.info-item span {
  font-size: 14px;
  color: var(--ion-color-dark);
  font-weight: 500;
}

.items-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.item-card {
  padding: 16px;
  border: 1px solid var(--ion-color-light-shade);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.item-card:hover {
  background: var(--ion-color-light-tint);
  border-color: var(--ion-color-primary-tint);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.item-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--ion-color-dark);
}

.item-status {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.item-status.claimed {
  background: var(--ion-color-success-tint);
  color: var(--ion-color-success-shade);
}

.item-status.unclaimed {
  background: var(--ion-color-warning-tint);
  color: var(--ion-color-warning-shade);
}

.status-icon {
  font-size: 14px;
}

.item-description {
  margin: 8px 0;
  font-size: 14px;
  color: var(--ion-color-medium-shade);
  line-height: 1.4;
}

.item-meta {
  display: flex;
  gap: 16px;
  margin: 8px 0;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--ion-color-medium);
}

.meta-icon {
  font-size: 14px;
}

.item-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  justify-content: flex-end;
}

.empty-items {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 32px 16px;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  color: var(--ion-color-medium);
}

.empty-items h3 {
  margin: 0;
  color: var(--ion-color-dark);
}

.empty-items p {
  margin: 0;
  color: var(--ion-color-medium);
  font-size: 14px;
}

.report-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 8px;
}

@media (max-width: 768px) {
  .report-details-container {
    padding: 12px;
  }

  .report-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .header-actions {
    justify-content: stretch;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .item-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }

  .item-actions {
    justify-content: flex-start;
  }
}

@media (min-width: 768px) {
  .report-actions {
    flex-direction: row;
    justify-content: center;
  }
}
</style>
