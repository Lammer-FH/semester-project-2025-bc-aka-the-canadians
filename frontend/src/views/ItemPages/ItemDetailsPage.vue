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
                :color="getStatusColor(item.status)"
                class="status-chip"
              >
                <ion-icon
                  :icon="getStatusIcon(item.status)"
                  class="chip-icon"
                ></ion-icon>
                {{ getStatusText(item.status) }}
              </ion-chip>
              <ion-chip v-if="item.report?.type" class="type-chip">
                <ion-icon
                  :icon="getTypeIcon(item.report.type)"
                  class="chip-icon"
                ></ion-icon>
                {{ getTypeText(item.report.type) }}
              </ion-chip>
              <div class="report-meta">
                <span class="report-id">Report #{{ item.id }}</span>
                <span class="report-date">{{
                  formatDate(item.createdAt || "")
                }}</span>
              </div>
            </div>
            <div class="header-right">
              <div class="days-since">
                <ion-icon :icon="timeOutline" class="time-icon"></ion-icon>
                <span>{{ daysSinceReported }} Days Ago</span>
              </div>
            </div>
          </div>
        </div>

        <div
          class="info-card main-info"
          :class="{ claimed: item.status === ItemStatus.CLAIMED }"
        >
          <div class="card-header">
            <h1 class="item-title">
              {{ item.name || "Unknown Item" }}
            </h1>
          </div>

          <div class="status-section">
            <div
              v-if="item.reportType === ReportType.FOUND"
              class="found-item-section"
            >
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
                  :color="
                    item.status === ItemStatus.CLAIMED ? 'medium' : 'success'
                  "
                  class="claim-button"
                  :disabled="item.status === ItemStatus.CLAIMED"
                  @click="claimItem"
                >
                  <ion-icon
                    :icon="
                      item.status === ItemStatus.CLAIMED
                        ? checkmarkCircleOutline
                        : handRightOutline
                    "
                    slot="start"
                  ></ion-icon>
                  {{
                    item.status === ItemStatus.CLAIMED
                      ? "Item Claimed"
                      : "Claim Item"
                  }}
                </ion-button>
              </div>
            </div>

            <div
              v-else-if="item.reportType === ReportType.LOST"
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
                  :color="
                    item.status === ItemStatus.CLAIMED ? 'medium' : 'warning'
                  "
                  class="report-found-button"
                  :disabled="item.status === ItemStatus.CLAIMED"
                  @click="claimItem"
                >
                  <ion-icon
                    :icon="
                      item.status === ItemStatus.CLAIMED
                        ? checkmarkCircleOutline
                        : megaphoneOutline
                    "
                    slot="start"
                  ></ion-icon>
                  {{
                    item.status === ItemStatus.CLAIMED
                      ? "Item Found"
                      : "I Found This Item"
                  }}
                </ion-button>
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

        <div
          class="info-card location-info"
          :class="{ claimed: item.status === ItemStatus.CLAIMED }"
        >
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
                item.locationName || "Unknown Location"
              }}</span>
            </div>
          </div>
        </div>

        <div class="action-buttons">
          <ion-button
            v-if="item.report?.type === ReportType.FOUND"
            expand="block"
            size="large"
            :color="item.status === ItemStatus.CLAIMED ? 'medium' : 'success'"
            :disabled="item.status === ItemStatus.CLAIMED"
            @click="claimItem"
          >
            <ion-icon
              :icon="
                item.status === ItemStatus.CLAIMED
                  ? checkmarkCircleOutline
                  : handRightOutline
              "
              slot="start"
            ></ion-icon>
            {{
              item.status === ItemStatus.CLAIMED ? "Item Claimed" : "Claim Item"
            }}
          </ion-button>

          <ion-button
            v-else-if="item.report?.type === ReportType.LOST"
            expand="block"
            size="large"
            fill="outline"
            :color="item.status === ItemStatus.CLAIMED ? 'medium' : 'warning'"
            :disabled="item.status === ItemStatus.CLAIMED"
            @click="claimItem"
          >
            <ion-icon
              :icon="
                item.status === ItemStatus.CLAIMED
                  ? checkmarkCircleOutline
                  : megaphoneOutline
              "
              slot="start"
            ></ion-icon>
            {{
              item.status === ItemStatus.CLAIMED
                ? "Item Claimed"
                : "Report as Found"
            }}
          </ion-button>
        </div>
      </div>
    </div>
  </template-page>
</template>

<script setup lang="ts">
import TemplatePage from "@/components/TemplatePage.vue";
import { IonIcon, IonButton, IonSpinner, IonChip } from "@ionic/vue";
import {
  arrowBackOutline,
  createOutline,
  alertCircleOutline,
  refreshOutline,
  documentTextOutline,
  locationOutline,
  businessOutline,
  timeOutline,
  eyeOutline,
  flagOutline,
  handRightOutline,
  searchOutline,
  megaphoneOutline,
  checkmarkCircleOutline,
  personOutline,
} from "ionicons/icons";
import { ref, computed, onMounted, nextTick } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useItemStore } from "@/stores/itemStore";
import { Item, ItemStatus } from "@/models/item";
import { ReportType } from "@/models/report";

const router = useRouter();
const route = useRoute();
const itemStore = useItemStore();

const item = ref<Item | null>(null);
const isLoading = ref(false);
const error = ref<string | null>(null);

const daysSinceReported = computed(() => {
  if (!item.value?.createdAt) return 0;
  const reportDate = new Date(item.value.createdAt);
  const now = new Date();
  const diffTime = Math.abs(now.getTime() - reportDate.getTime());
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
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

const getStatusColor = (status: ItemStatus): string => {
  switch (status) {
    case ItemStatus.UNCLAIMED:
      return "success";
    case ItemStatus.CLAIMED:
      return "medium";
    default:
      return "primary";
  }
};

const getStatusIcon = (status: ItemStatus): string => {
  switch (status) {
    case ItemStatus.UNCLAIMED:
      return eyeOutline;
    case ItemStatus.CLAIMED:
      return checkmarkCircleOutline;
    default:
      return flagOutline;
  }
};

const getStatusText = (status: ItemStatus): string => {
  switch (status) {
    case ItemStatus.UNCLAIMED:
      return "Unclaimed";
    case ItemStatus.CLAIMED:
      return "Claimed";
    default:
      return "Unknown";
  }
};

const getTypeText = (type: ReportType): string => {
  switch (type) {
    case ReportType.LOST:
      return "Lost";
    case ReportType.FOUND:
      return "Found";
    default:
      return "Unknown";
  }
};

const getTypeIcon = (type: ReportType): string => {
  switch (type) {
    case ReportType.LOST:
      return searchOutline;
    case ReportType.FOUND:
      return eyeOutline;
    default:
      return flagOutline;
  }
};

const formatDate = (dateString: string) => {
  if (!dateString) return "Unknown Date";
  return new Date(dateString).toLocaleDateString("en-US", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const getCleanDescription = (description: string): string => {
  if (!description) return "No description available.";
  const parts = description.split("--- Report Information ---");
  return parts[0]?.trim() || "No description available.";
};

const getReporterInfo = (
  description: string
): Record<string, string> | null => {
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
};

const handleBack = () => {
  router.back();
};

const handleEdit = () => {
  if (item.value?.id) {
    router.push(`/items/${item.value.id}/edit`);
  }
};

// right now this handles both the "found item" and the "claim item" use cases;
// this would be disambiguated in a later iteration
const claimItem = async () => {
  if (!item.value) return;

  try {
    const currentUserId = 1;

    isLoading.value = true;
    error.value = null;

    const updatedItem = await itemStore.updateItem(item.value.id, {
      claimedByUserId: currentUserId,
      status: ItemStatus.CLAIMED,
    });

    if (updatedItem) {
      item.value = updatedItem;
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : "Failed to claim item";
    console.error("Error claiming item:", err);
  } finally {
    isLoading.value = false;
  }
};

onMounted(async () => {
  await loadItem();
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
  --background: rgba(255, 255, 255, 0.95);
  --color: var(--ion-color-dark);
  margin-bottom: 8px;
  font-weight: 600;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.status-chip[color="success"] {
  --background: var(--ion-color-success);
  --color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.status-chip[color="warning"] {
  --background: var(--ion-color-warning);
  --color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
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

.days-since {
  display: flex;
  align-items: center;
  gap: 4px;
  justify-content: flex-end;
}

.time-icon {
  font-size: 14px;
}

.info-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.info-card.claimed {
  background: #fafafa;
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

.claimed-banner {
  background: linear-gradient(
    135deg,
    var(--ion-color-light),
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

.timeline-type {
  color: var(--ion-color-primary);
  font-size: 0.8em;
  font-weight: 500;
  background: var(--ion-color-primary-tint);
  padding: 2px 8px;
  border-radius: 12px;
  display: inline-block;
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

@keyframes buttonClick {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(0.95);
  }
  100% {
    transform: scale(1);
  }
}

.claim-button:active,
.report-found-button:active {
  animation: buttonClick 0.2s ease-in-out;
}

.claim-button:disabled,
.report-found-button:disabled {
  animation: none;
  opacity: 0.6;
  transform: scale(0.98);
  transition: all 0.3s ease;
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

  .days-since {
    justify-content: flex-start;
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
    grid-template-columns: repeat(
      2,
      1fr
    ); /* Changed from 3 to 2 since there are only 2 buttons */
  }
}
</style>
