<template>
  <template-page
    :headline="item?.name || 'Item Details'"
    :leftFooterButton="leftFooterButton"
    :rightFooterButton="rightFooterButton"
    @leftFooterButtonClicked="handleBack"
    @rightFooterButtonClicked="handleEdit"
  >
    <div class="details-container">
      <div v-if="isLoading" class="loading-container">
        <ion-spinner name="crescent" color="primary"></ion-spinner>
        <p>Loading report...</p>
      </div>

      <div v-else-if="error && !item" class="empty-state">
        <ion-icon :icon="alertCircleOutline" class="empty-icon"></ion-icon>
        <h2>Error Loading</h2>
        <p>{{ error }}</p>
        <ion-button @click="loadItem">
          <ion-icon :icon="refreshOutline" slot="start"></ion-icon>
          Try Again
        </ion-button>
      </div>

      <div v-else-if="item" class="item-details">
        <div class="report-header">
          <div class="header-content">
            <div class="header-left">
              <ion-chip
                :color="getStatusColor(item.report?.status ?? false)"
                class="status-chip"
              >
                <ion-icon
                  :icon="getStatusIcon(item.report?.status ?? false)"
                  class="chip-icon"
                ></ion-icon>
                {{ getStatusText(item.report?.status ?? false) }}
              </ion-chip>
              <div class="report-meta">
                <span class="report-id">Report #{{ item.id }}</span>
                <span class="report-date">{{
                  formatDate(item.createdAt || "")
                }}</span>
              </div>
            </div>
            <div class="header-right">
              <div class="view-count">
                <ion-icon :icon="eyeOutline" class="view-icon"></ion-icon>
                <span>{{ viewCount }} Views</span>
              </div>
              <div class="days-since">
                <ion-icon :icon="timeOutline" class="time-icon"></ion-icon>
                <span>{{ daysSinceReported }} Days Ago</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Image functionality removed - not available in current schema -->

        <div class="info-card main-info">
          <div class="card-header">
            <h1 class="item-title">
              {{ item.name || "Unknown Item" }}
            </h1>
            <div class="title-actions">
              <ion-button fill="clear" size="small" @click="shareItem">
                <ion-icon :icon="shareOutline" slot="icon-only"></ion-icon>
              </ion-button>
            </div>
          </div>

          <div class="status-section">
            <div v-if="item.report?.status === true" class="found-item-section">
              <div class="action-banner found-banner">
                <div class="banner-content">
                  <ion-icon :icon="eyeOutline" class="banner-icon"></ion-icon>
                  <div class="banner-text">
                    <h3>Found Item</h3>
                    <p>
                      Is this your lost item? You can request to pick it up.
                    </p>
                  </div>
                </div>
                <ion-button
                  expand="block"
                  size="large"
                  color="success"
                  class="claim-button"
                  @click="showClaimDialog"
                >
                  <ion-icon :icon="handRightOutline" slot="start"></ion-icon>
                  Claim Item
                </ion-button>
              </div>
            </div>

            <div
              v-else-if="item.report?.status === false && !item.claimedByUserId"
              class="lost-item-section"
            >
              <div class="action-banner lost-banner">
                <div class="banner-content">
                  <ion-icon
                    :icon="searchOutline"
                    class="banner-icon"
                  ></ion-icon>
                  <div class="banner-text">
                    <h3>Lost Item</h3>
                    <p>
                      If you found this item, please report it to help reunite
                      it with the owner.
                    </p>
                  </div>
                </div>
                <ion-button
                  expand="block"
                  size="large"
                  color="warning"
                  class="report-found-button"
                  @click="reportFound"
                >
                  <ion-icon :icon="megaphoneOutline" slot="start"></ion-icon>
                  I Found This Item
                </ion-button>
              </div>
            </div>

            <div v-else-if="item.claimedByUserId" class="claimed-item-section">
              <div class="action-banner claimed-banner">
                <div class="banner-content">
                  <ion-icon
                    :icon="checkmarkCircleOutline"
                    class="banner-icon"
                  ></ion-icon>
                  <div class="banner-text">
                    <h3>Claimed</h3>
                    <p>This item has already been requested for pickup.</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div v-if="item.description" class="description-section">
            <h3>
              <ion-icon
                :icon="documentTextOutline"
                class="section-icon"
              ></ion-icon>
              Description
            </h3>
            <div class="description-content">
              <p class="description-text">
                {{ getCleanDescription(item.description) }}
              </p>

              <div
                v-if="getReporterInfo(item.description)"
                class="reporter-info"
              >
                <h4>
                  <ion-icon :icon="personOutline" class="info-icon"></ion-icon>
                  Report Details
                </h4>
                <div class="reporter-details">
                  <div
                    v-for="(info, key) in getReporterInfo(item.description)"
                    :key="key"
                    class="info-row"
                  >
                    <span class="info-label">{{ key }}:</span>
                    <span class="info-value">{{ info }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="info-card location-info">
          <h3>
            <ion-icon :icon="locationOutline" class="section-icon"></ion-icon>
            Location
          </h3>
          <div class="location-details">
            <div class="location-primary">
              <ion-icon
                :icon="businessOutline"
                class="location-icon"
              ></ion-icon>
              <span class="location-name">{{
                item.report?.location?.name || "Unknown Location"
              }}</span>
            </div>
            <ion-button fill="clear" size="small" @click="viewLocationReports">
              <ion-icon :icon="flagOutline" slot="start"></ion-icon>
              More Reports at this Location
            </ion-button>
          </div>
        </div>

        <div class="info-card timeline-info">
          <h3>
            <ion-icon :icon="timeOutline" class="section-icon"></ion-icon>
            Report History
          </h3>
          <div class="timeline">
            <div class="timeline-item">
              <div class="timeline-marker created"></div>
              <div class="timeline-content">
                <h4>Report Created</h4>
                <p>{{ formatDetailedDate(item.createdAt || "") }}</p>
                <span class="timeline-type">{{
                  getReportType(item.report?.status ?? false)
                }}</span>
              </div>
            </div>
            <!-- Status update timeline removed - updatedAt not available in schema -->
            <div v-if="item.claimedByUserId" class="timeline-item">
              <div class="timeline-marker claimed"></div>
              <div class="timeline-content">
                <h4>Pickup Requested</h4>
                <p>Waiting for confirmation</p>
              </div>
            </div>
          </div>
        </div>

        <div v-if="showStatistics" class="info-card stats-info">
          <h3>
            <ion-icon :icon="statsChartOutline" class="section-icon"></ion-icon>
            Statistics
          </h3>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-number">{{ viewCount }}</div>
              <div class="stat-label">Views</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ daysSinceReported }}</div>
              <div class="stat-label">Days Old</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ getLocationReportsCount() }}</div>
              <div class="stat-label">Reports at Location</div>
            </div>
          </div>
        </div>

        <div v-if="relatedReports.length > 0" class="info-card related-reports">
          <h3>
            <ion-icon :icon="layersOutline" class="section-icon"></ion-icon>
            Similar Reports
          </h3>
          <div class="related-list">
            <div
              v-for="related in relatedReports"
              :key="related.id"
              class="related-item"
              @click="navigateToReport(related.id)"
            >
              <div class="related-content">
                <ion-chip
                  :color="getStatusColor(related.report?.status ?? false)"
                  class="related-status"
                >
                  <ion-icon
                    :icon="getStatusIcon(related.report?.status ?? false)"
                    class="chip-icon"
                  ></ion-icon>
                  {{ getStatusText(related.report?.status ?? false) }}
                </ion-chip>
                <h4>{{ related.name || "Unknown Item" }}</h4>
                <p>
                  {{ related.report?.location?.name || "Unknown Location" }} •
                  {{ getTimeAgo(related.createdAt || "") }}
                </p>
              </div>
              <ion-icon
                :icon="chevronForwardOutline"
                class="chevron-icon"
              ></ion-icon>
            </div>
          </div>
        </div>

        <div class="action-buttons">
          <ion-button
            v-if="item.report?.status === true"
            expand="block"
            size="large"
            color="success"
            @click="showClaimDialog"
          >
            <ion-icon :icon="handRightOutline" slot="start"></ion-icon>
            Claim Item
          </ion-button>

          <ion-button
            v-else-if="item.report?.status === false"
            expand="block"
            size="large"
            fill="outline"
            color="warning"
            @click="reportFound"
          >
            <ion-icon :icon="megaphoneOutline" slot="start"></ion-icon>
            Report as Found
          </ion-button>

          <div class="secondary-actions">
            <ion-button expand="block" fill="outline" @click="shareItem">
              <ion-icon :icon="shareOutline" slot="start"></ion-icon>
              Share Report
            </ion-button>

            <ion-button
              expand="block"
              fill="outline"
              color="medium"
              @click="toggleStatistics"
            >
              <ion-icon :icon="statsChartOutline" slot="start"></ion-icon>
              {{ showStatistics ? "Hide Statistics" : "Show Statistics" }}
            </ion-button>

            <ion-button
              expand="block"
              fill="outline"
              color="danger"
              @click="showDeleteConfirmation"
            >
              <ion-icon :icon="trashOutline" slot="start"></ion-icon>
              Delete Report
            </ion-button>
          </div>
        </div>
      </div>
    </div>

    <!-- Image modal removed - imageUrl not available in schema -->

    <ion-alert
      :is-open="showClaimAlert"
      header="Claim Item"
      :message="claimAlertMessage"
      :buttons="claimAlertButtons"
      @didDismiss="showClaimAlert = false"
    ></ion-alert>

    <ion-alert
      :is-open="showDeleteAlert"
      header="Delete Report"
      message="Do you really want to delete this report? This action cannot be undone."
      :buttons="deleteAlertButtons"
      @didDismiss="showDeleteAlert = false"
    ></ion-alert>
  </template-page>
</template>

<script setup lang="ts">
import TemplatePage from "@/components/TemplatePage.vue";
import { IonIcon, IonButton, IonSpinner, IonChip, IonAlert } from "@ionic/vue";
import {
  arrowBackOutline,
  createOutline,
  alertCircleOutline,
  refreshOutline,
  documentTextOutline,
  locationOutline,
  businessOutline,
  timeOutline,
  shareOutline,
  trashOutline,
  statsChartOutline,
  eyeOutline,
  flagOutline,
  handRightOutline,
  searchOutline,
  megaphoneOutline,
  checkmarkCircleOutline,
  personOutline,
  layersOutline,
  chevronForwardOutline,
} from "ionicons/icons";
import { ref, computed, onMounted, nextTick } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useItemStore } from "@/stores/itemStore";
import type { Item } from "@/models/item";

const router = useRouter();
const route = useRoute();
const itemStore = useItemStore();

const item = ref<Item | null>(null);
const relatedReports = ref<Item[]>([]);
// Image functionality removed - not available in current schema
const showDeleteAlert = ref(false);
const showClaimAlert = ref(false);
const showStatistics = ref(false);
const viewCount = ref(0);
const isLoading = ref(false);
const error = ref<string | null>(null);

const daysSinceReported = computed(() => {
  try {
    if (!item.value?.createdAt) return 0;
    const reportDate = new Date(item.value.createdAt);
    const now = new Date();
    const diffTime = Math.abs(now.getTime() - reportDate.getTime());
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  } catch (error) {
    console.error("Error calculating days since reported:", error);
    return 0;
  }
});

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

const deleteAlertButtons = [
  {
    text: "Cancel",
    role: "cancel",
    cssClass: "alert-button-cancel",
  },
  {
    text: "Delete",
    cssClass: "alert-button-confirm",
    handler: () => confirmDelete(),
  },
];

const claimAlertMessage = computed(() => {
  try {
    if (!item.value) return "Loading...";

    return `
            <div style="text-align: left; padding: 8px;">
                <p><strong>Item:</strong> ${item.value.name || "Unknown"}</p>
                <p><strong>Location:</strong> ${
                  item.value.report?.location?.name || "Unknown"
                }</p>
                <br>
                <p>Do you want to request this item for pickup?</p>
                <p style="color: #666; font-size: 0.9em;">
                    The finder will be notified and can contact you
                    to coordinate the pickup.
                </p>
            </div>
        `;
  } catch (error) {
    console.error("Error generating claim alert message:", error);
    return "Error loading message.";
  }
});

const claimAlertButtons = [
  {
    text: "Cancel",
    role: "cancel",
    cssClass: "alert-button-cancel",
  },
  {
    text: "Yes, Claim",
    cssClass: "alert-button-confirm",
    handler: () => processClaim(),
  },
];

const loadItem = async () => {
  try {
    isLoading.value = true;
    error.value = null;

    const itemId = parseInt(route.params.id as string);
    if (isNaN(itemId)) {
      throw new Error("Invalid item ID");
    }

    await itemStore.fetchItemById(itemId);
    const fetchedItem = itemStore.getCurrentItem;

    if (fetchedItem) {
      await nextTick();
      item.value = { ...fetchedItem };
      await loadRelatedReports();
      await loadItemStatistics();
    } else {
      throw new Error("Item not found");
    }
  } catch (err) {
    console.error("Error loading item:", err);
    error.value = err instanceof Error ? err.message : "Unknown error";
    setTimeout(() => {
      router.back();
    }, 2000);
  } finally {
    isLoading.value = false;
  }
};

const loadRelatedReports = async () => {
  if (!item.value) return;

  try {
    await itemStore.fetchItems();
    const allItems = itemStore.getItems || [];

    relatedReports.value = allItems
      .filter(
        i =>
          i.id !== item.value!.id &&
          i.report?.locationId === item.value!.report?.locationId &&
          !i.claimedByUserId
      )
      .slice(0, 3);
  } catch (error) {
    console.error("Error loading related reports:", error);
    relatedReports.value = [];
  }
};

const loadItemStatistics = async () => {
  try {
    viewCount.value = Math.floor(Math.random() * 50) + 5;
  } catch (error) {
    console.error("Error loading statistics:", error);
    viewCount.value = 0;
  }
};

const getStatusColor = (status: boolean): string => {
  try {
    return status ? "success" : "warning";
  } catch (error) {
    console.error("Error getting status color:", error);
    return "primary";
  }
};

const getStatusIcon = (status: boolean): string => {
  try {
    return status ? eyeOutline : searchOutline;
  } catch (error) {
    console.error("Error getting status icon:", error);
    return flagOutline;
  }
};

const getStatusText = (status: boolean): string => {
  try {
    return status ? "Found" : "Lost";
  } catch (error) {
    console.error("Error getting status text:", error);
    return "Unknown";
  }
};

const getReportType = (status: boolean): string => {
  try {
    return status ? "Found Report" : "Lost Report";
  } catch (error) {
    console.error("Error getting report type:", error);
    return "Report";
  }
};

const formatDate = (dateString: string) => {
  try {
    if (!dateString) return "Unknown Date";
    return new Date(dateString).toLocaleDateString("en-US", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  } catch (error) {
    console.error("Error formatting date:", error);
    return "Invalid Date";
  }
};

const formatDetailedDate = (dateString: string) => {
  try {
    if (!dateString) return "Unknown Date";
    return new Date(dateString).toLocaleDateString("en-US", {
      weekday: "long",
      day: "2-digit",
      month: "long",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  } catch (error) {
    console.error("Error formatting detailed date:", error);
    return "Invalid Date";
  }
};

const getTimeAgo = (dateString: string) => {
  try {
    if (!dateString) return "Unknown";

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
  } catch (error) {
    console.error("Error getting time ago:", error);
    return "Unknown";
  }
};

const getCleanDescription = (description: string): string => {
  try {
    if (!description) return "No description available.";
    const parts = description.split("--- Report Information ---");
    return parts[0]?.trim() || "No description available.";
  } catch (error) {
    console.error("Error getting clean description:", error);
    return "No description available.";
  }
};

const getReporterInfo = (
  description: string
): Record<string, string> | null => {
  try {
    if (!description) return null;

    const metadataMatch = description.match(
      /--- Report Information ---([\s\S]*?)(?:--- |$)/
    );
    if (!metadataMatch) return null;

    const metadata = metadataMatch[1];
    const info: Record<string, string> = {};

    const lines = metadata.split("\n").filter(line => line.trim());
    lines.forEach(line => {
      const [key, ...valueParts] = line.split(":");
      if (key && valueParts.length > 0) {
        info[key.trim()] = valueParts.join(":").trim();
      }
    });

    return Object.keys(info).length > 0 ? info : null;
  } catch (error) {
    console.error("Error getting reporter info:", error);
    return null;
  }
};

const getLocationReportsCount = (): number => {
  try {
    return relatedReports.value.length + 1; // +1 for current item
  } catch (error) {
    console.error("Error getting location reports count:", error);
    return 1;
  }
};

const toggleStatistics = () => {
  try {
    showStatistics.value = !showStatistics.value;
  } catch (error) {
    console.error("Error toggling statistics:", error);
  }
};

const handleBack = () => {
  try {
    router.back();
  } catch (error) {
    console.error("Error navigating back:", error);
    router.push("/items/overview");
  }
};

const handleEdit = () => {
  try {
    if (item.value?.id) {
      router.push(`/items/${item.value.id}/edit`);
    }
  } catch (error) {
    console.error("Error navigating to edit:", error);
  }
};

// Image modal functions removed - not available in current schema

const showDeleteConfirmation = () => {
  try {
    showDeleteAlert.value = true;
  } catch (error) {
    console.error("Error showing delete confirmation:", error);
  }
};

const confirmDelete = async () => {
  if (!item.value?.id) return;

  try {
    const success = await itemStore.deleteItem(item.value.id);
    if (success) {
      router.push("/items/overview");
    }
  } catch (error) {
    console.error("Error deleting item:", error);
  }
};

const showClaimDialog = () => {
  try {
    showClaimAlert.value = true;
  } catch (error) {
    console.error("Error showing claim dialog:", error);
  }
};

const processClaim = async () => {
  if (!item.value?.id) return;

  try {
    const currentDate = new Date().toLocaleDateString("en-US");
    const claimDescription = [
      "--- PICKUP REQUESTED ---",
      `Requested on: ${currentDate}`,
      "Status: Waiting for finder confirmation",
      "",
      "--- ORIGINAL DESCRIPTION ---",
      item.value.description || "",
    ].join("\n");

    const updatedItem = await itemStore.updateItem(item.value.id, {
      description: claimDescription,
    });

    if (updatedItem) {
      item.value = updatedItem;
    }

    console.log("Item claimed successfully");
  } catch (error) {
    console.error("Error claiming item:", error);
  }
};

const reportFound = () => {
  try {
    if (!item.value) return;
    router.push({
      path: "/items/report",
      query: {
        type: "FOUND",
        name: item.value.name || "",
        location: item.value.report?.location?.name || "",
      },
    });
  } catch (error) {
    console.error("Error navigating to create found report:", error);
  }
};

const shareItem = async () => {
  if (!item.value) return;

  try {
    const shareData = {
      title: `Lost & Found: ${item.value.name || "Unbekannt"}`,
      text: `${getStatusText(item.value.report?.status ?? false)}: ${
        item.value.name || "Unknown"
      } at ${item.value.report?.location?.name || "Unknown"}`,
      url: window.location.href,
    };

    if (navigator.share) {
      await navigator.share(shareData);
    } else {
      await navigator.clipboard.writeText(window.location.href);
      console.log("Link copied to clipboard");
    }
  } catch (error) {
    console.error("Error sharing:", error);
  }
};

const viewLocationReports = () => {
  try {
    router.push({
      path: "/items/overview",
      query: { location: item.value?.report?.location?.name || "" },
    });
  } catch (error) {
    console.error("Error navigating to location reports:", error);
  }
};

const navigateToReport = (_reportId: number) => {
  try {
    if (_reportId) {
      router.push(`/items/${_reportId}`);
    }
  } catch (error) {
    console.error("Error navigating to report:", error);
  }
};

onMounted(async () => {
  try {
    await loadItem();
  } catch (error) {
    console.error("Error in onMounted:", error);
  }
});
</script>

<style scoped>
.details-container {
  padding: 16px;
  max-width: 800px;
  margin: 0 auto;
  min-height: 100vh;
  background: var(--ion-color-light-tint);
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
}

.empty-icon {
  font-size: 64px;
  color: var(--ion-color-medium);
  margin-bottom: 20px;
}

.item-details {
  animation: slideInUp 0.6s ease-out;
}

.report-header {
  background: linear-gradient(
    135deg,
    var(--ion-color-primary),
    var(--ion-color-primary-shade)
  );
  color: white;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 4px 12px rgba(var(--ion-color-primary-rgb), 0.3);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.header-left {
  flex: 1;
}

.status-chip {
  --background: rgba(255, 255, 255, 0.2);
  --color: white;
  margin-bottom: 8px;
}

.chip-icon {
  font-size: 14px;
  margin-right: 4px;
}

.report-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 0.9em;
  opacity: 0.9;
}

.report-id {
  font-weight: 600;
}

.report-date {
  opacity: 0.8;
}

.header-right {
  display: flex;
  flex-direction: column;
  gap: 8px;
  text-align: right;
  font-size: 0.85em;
  opacity: 0.9;
}

.view-count,
.days-since {
  display: flex;
  align-items: center;
  gap: 4px;
  justify-content: flex-end;
}

.view-icon,
.time-icon {
  font-size: 14px;
}

.image-section {
  margin-bottom: 16px;
}

.image-container {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.item-image {
  width: 100%;
  height: 300px;
  object-fit: cover;
  transition: transform 0.3s ease;
  cursor: pointer;
}

.item-image:hover {
  transform: scale(1.02);
}

.expand-button {
  position: absolute;
  top: 12px;
  right: 12px;
  --background: rgba(0, 0, 0, 0.5);
  --color: white;
  --border-radius: 50%;
  backdrop-filter: blur(10px);
}

.image-overlay {
  position: absolute;
  bottom: 12px;
  left: 12px;
}

.image-chip {
  --background: rgba(0, 0, 0, 0.7);
  --color: white;
  backdrop-filter: blur(10px);
}

.info-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid var(--ion-color-light-shade);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.item-title {
  color: var(--ion-color-dark);
  margin: 0;
  font-size: 1.8em;
  font-weight: 600;
  line-height: 1.2;
}

.title-actions {
  display: flex;
  gap: 8px;
}

.status-section {
  margin-bottom: 20px;
}

.action-banner {
  border-radius: 12px;
  padding: 20px;
  border-left: 4px solid;
}

.found-banner {
  background: linear-gradient(
    135deg,
    var(--ion-color-success-tint),
    rgba(var(--ion-color-success-rgb), 0.1)
  );
  border-left-color: var(--ion-color-success);
}

.lost-banner {
  background: linear-gradient(
    135deg,
    var(--ion-color-warning-tint),
    rgba(var(--ion-color-warning-rgb), 0.1)
  );
  border-left-color: var(--ion-color-warning);
}

.claimed-banner,
.returned-banner {
  background: linear-gradient(
    135deg,
    var(--ion-color-medium-tint),
    rgba(var(--ion-color-medium-rgb), 0.1)
  );
  border-left-color: var(--ion-color-medium);
}

.banner-content {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 16px;
}

.banner-icon {
  font-size: 24px;
  margin-top: 4px;
  flex-shrink: 0;
}

.banner-text h3 {
  color: var(--ion-color-dark);
  margin: 0 0 4px 0;
  font-weight: 600;
}

.banner-text p {
  color: var(--ion-color-medium-shade);
  margin: 0;
  line-height: 1.4;
}

.claim-button {
  --background: var(--ion-color-success);
  --color: white;
  font-weight: 600;
  font-size: 1.1em;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.02);
  }
  100% {
    transform: scale(1);
  }
}

.description-section h3 {
  color: var(--ion-color-dark);
  margin: 0 0 16px 0;
  font-size: 1.1em;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-icon {
  font-size: 18px;
  color: var(--ion-color-primary);
}

.description-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.description-text {
  color: var(--ion-color-medium-shade);
  line-height: 1.6;
  margin: 0;
  font-size: 1em;
}

.reporter-info {
  background: var(--ion-color-light-tint);
  border-radius: 8px;
  padding: 16px;
  border-left: 4px solid var(--ion-color-primary);
}

.reporter-info h4 {
  color: var(--ion-color-dark);
  margin: 0 0 12px 0;
  font-size: 1em;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-icon {
  font-size: 16px;
  color: var(--ion-color-primary);
}

.reporter-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9em;
}

.info-label {
  color: var(--ion-color-medium);
  font-weight: 500;
}

.info-value {
  color: var(--ion-color-dark);
  text-align: right;
}

.location-details {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.location-primary {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 1.1em;
}

.location-icon {
  font-size: 20px;
  color: var(--ion-color-primary);
}

.location-name {
  color: var(--ion-color-dark);
  font-weight: 500;
}

.timeline {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.timeline-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  position: relative;
}

.timeline-item:not(:last-child)::after {
  content: "";
  position: absolute;
  left: 11px;
  top: 24px;
  bottom: -16px;
  width: 2px;
  background: var(--ion-color-light-shade);
}

.timeline-marker {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: 3px solid;
  background: white;
  flex-shrink: 0;
  margin-top: 2px;
}

.timeline-marker.created {
  border-color: var(--ion-color-primary);
}

.timeline-marker.updated {
  border-color: var(--ion-color-warning);
}

.timeline-marker.claimed {
  border-color: var(--ion-color-success);
}

.timeline-content h4 {
  color: var(--ion-color-dark);
  margin: 0 0 4px 0;
  font-weight: 600;
  font-size: 0.95em;
}

.timeline-content p {
  color: var(--ion-color-medium);
  margin: 0 0 4px 0;
  font-size: 0.9em;
}

.timeline-type,
.timeline-status {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 0.75em;
  font-weight: 500;
  background: var(--ion-color-light-shade);
  color: var(--ion-color-medium-shade);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.stat-item {
  text-align: center;
  background: var(--ion-color-light-tint);
  padding: 16px;
  border-radius: 8px;
}

.stat-number {
  font-size: 1.8em;
  font-weight: 700;
  color: var(--ion-color-primary);
  margin-bottom: 4px;
}

.stat-label {
  color: var(--ion-color-medium);
  font-size: 0.85em;
  font-weight: 500;
}

.related-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.related-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: var(--ion-color-light-tint);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.related-item:hover {
  background: var(--ion-color-light-shade);
  transform: translateX(4px);
}

.related-content {
  flex: 1;
}

.related-status {
  font-size: 0.8em;
  margin-bottom: 4px;
}

.related-content h4 {
  color: var(--ion-color-dark);
  margin: 0 0 4px 0;
  font-weight: 600;
  font-size: 0.95em;
}

.related-content p {
  color: var(--ion-color-medium);
  margin: 0;
  font-size: 0.85em;
}

.chevron-icon {
  color: var(--ion-color-medium);
  font-size: 16px;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 24px;
}

.secondary-actions {
  display: grid;
  grid-template-columns: 1fr;
  gap: 8px;
  margin-top: 8px;
}

.modal-content {
  background: black;
}

.modal-image-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 20px;
}

.modal-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

:global(.alert-button-confirm) {
  color: var(--ion-color-success) !important;
  font-weight: 600 !important;
}

:global(.alert-button-cancel) {
  color: var(--ion-color-medium) !important;
}

@keyframes slideInUp {
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
  .details-container {
    padding: 12px;
  }

  .header-content {
    flex-direction: column;
    gap: 12px;
  }

  .header-right {
    align-self: stretch;
    text-align: left;
  }

  .view-count,
  .days-since {
    justify-content: flex-start;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .secondary-actions {
    grid-template-columns: 1fr;
  }

  .banner-content {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .item-title {
    font-size: 1.5em;
  }

  .card-header {
    flex-direction: column;
    gap: 12px;
  }

  .title-actions {
    align-self: stretch;
    justify-content: center;
  }

  .info-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .info-value {
    text-align: left;
  }
}

@media (min-width: 768px) {
  .secondary-actions {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>
