<template>
  <template-page
    :headline="'Edit Report'"
    :leftFooterButton="leftFooterButton"
    :rightFooterButton="rightFooterButton"
    @leftFooterButtonClicked="handleCancel"
    @rightFooterButtonClicked="handleSave"
  >
    <div class="form-container">
      <div v-if="isLoading" class="loading-container">
        <ion-spinner name="crescent" color="primary"></ion-spinner>
        <p>Loading report...</p>
      </div>

      <div v-else-if="error && !report.id" class="empty-state">
        <ion-icon :icon="alertCircleOutline" class="empty-icon"></ion-icon>
        <h2>Error Loading</h2>
        <p>{{ error }}</p>
        <ion-button @click="loadReport">
          <ion-icon :icon="refreshOutline" slot="start"></ion-icon>
          Try Again
        </ion-button>
      </div>
      <div v-else class="form-content">
        <!-- Resolved Report Warning -->
        <div
          v-if="report.status === ReportStatusEnum.RESOLVED"
          class="resolved-warning"
        >
          <ion-icon :icon="lockClosedOutline" class="warning-icon"></ion-icon>
          <div class="warning-content">
            <h3>Report is Resolved</h3>
            <p>
              This report cannot be edited because it has been resolved. All
              items have been claimed and the report is now immutable.
            </p>
          </div>
        </div>

        <div class="form-header">
          <ion-icon :icon="createOutline" class="header-icon"></ion-icon>
          <p>Update the details for this report</p>
          <div v-if="report.createdAt" class="last-modified">
            <ion-icon :icon="timeOutline" class="time-icon"></ion-icon>
            Created: {{ formatDate(report.createdAt) }}
          </div>
        </div>

        <!-- Editable Report Fields -->
        <div class="editable-section">
          <h3 class="section-header">
            <ion-icon :icon="createOutline" class="section-icon"></ion-icon>
            Editable Report Fields
          </h3>
          <p class="section-description">
            These report-specific fields can be edited:
          </p>
        </div>
        <div class="input-group">
          <ion-select
            v-model="report.locationId"
            label="Location *"
            label-placement="stacked"
            placeholder="Choose a location"
            interface="popover"
            class="modern-select"
            :class="{
              'select-filled': report.locationId,
              'select-error': errors.locationId,
            }"
            :disabled="report.status === ReportStatusEnum.RESOLVED"
            @ionChange="validateField('locationId')"
          >
            <ion-icon
              :icon="locationOutline"
              slot="start"
              class="input-icon"
            ></ion-icon>
            <ion-select-option
              v-for="location in availableLocations"
              :key="location.id"
              :value="location.id"
            >
              <div class="select-option">
                <ion-icon
                  :icon="businessOutline"
                  class="option-icon"
                ></ion-icon>
                <span>{{ location.name }}</span>
              </div>
            </ion-select-option>
          </ion-select>
          <div v-if="errors.locationId" class="error-message">
            <ion-icon :icon="alertCircleOutline"></ion-icon>
            {{ errors.locationId }}
          </div>
        </div>

        <div class="input-group">
          <ion-select
            v-model="report.status"
            label="Report Status *"
            label-placement="stacked"
            placeholder="Choose report status"
            interface="popover"
            class="modern-select"
            :class="{
              'select-filled': report.status !== undefined,
              'select-error': errors.status,
            }"
            @ionChange="validateField('status')"
          >
            <ion-icon
              :icon="flagOutline"
              slot="start"
              class="input-icon"
            ></ion-icon>
            <ion-select-option :value="ReportStatusEnum.OPEN">
              <div class="select-option">
                <ion-icon :icon="searchOutline" class="option-icon"></ion-icon>
                <div>
                  <p>Items still available</p>
                </div>
              </div>
            </ion-select-option>
            <ion-select-option
              :value="ReportStatusEnum.RESOLVED"
              :disabled="!canBeResolved"
            >
              <div class="select-option">
                <ion-icon
                  :icon="checkmarkCircleOutline"
                  class="option-icon"
                ></ion-icon>
                <div>
                  <p>All items have been claimed</p>
                </div>
              </div>
            </ion-select-option>
          </ion-select>
          <div v-if="errors.status" class="error-message">
            <ion-icon :icon="alertCircleOutline"></ion-icon>
            {{ errors.status }}
          </div>
        </div>
        <!-- Items Management Section -->
        <div class="divider"></div>

        <div class="items-section">
          <div class="section-header">
            <h3 class="section-title">
              <ion-icon :icon="cubeOutline" class="section-icon"></ion-icon>
              Items in this Report
            </h3>
          </div>
          <p class="section-description">
            Manage the items associated with this report. You can edit existing
            items or remove items from the report.
          </p>

          <div
            v-if="report.items && report.items.length > 0"
            class="items-list"
          >
            <div v-for="item in report.items" :key="item.id" class="item-card">
              <div class="item-header">
                <div class="item-info">
                  <h4 class="item-name">{{ item.name }}</h4>
                  <div class="item-status">
                    <ion-icon
                      :icon="getItemStatusIcon(item.status)"
                      class="status-icon"
                      :class="getItemStatusClass(item.status)"
                    ></ion-icon>
                    <span :class="getItemStatusClass(item.status)">
                      {{ getItemStatusText(item.status) }}
                    </span>
                  </div>
                </div>
                <div class="item-actions">
                  <ion-button
                    fill="clear"
                    size="small"
                    color="primary"
                    @click="navigateToItemDetails(item.id)"
                    title="View Item Details"
                  >
                    <ion-icon :icon="eyeOutline" slot="icon-only"></ion-icon>
                  </ion-button>
                  <ion-button
                    fill="clear"
                    size="small"
                    color="primary"
                    @click="navigateToEditItem(item.id)"
                    title="Edit Item"
                    :disabled="report.status === ReportStatusEnum.RESOLVED"
                  >
                    <ion-icon :icon="createOutline" slot="icon-only"></ion-icon>
                  </ion-button>
                  <ion-button
                    fill="clear"
                    size="small"
                    color="danger"
                    @click="confirmRemoveItem(item)"
                    title="Remove Item from Report"
                    :disabled="report.status === ReportStatusEnum.RESOLVED"
                  >
                    <ion-icon :icon="trashOutline" slot="icon-only"></ion-icon>
                  </ion-button>
                </div>
              </div>
              <p v-if="item.description" class="item-description">
                {{ truncateText(item.description, 100) }}
              </p>
            </div>
          </div>
          <div v-else class="empty-items">
            <ion-icon :icon="cubeOutline" class="empty-icon"></ion-icon>
            <h4>No Items in Report</h4>
            <p>This report doesn't have any items yet.</p>
          </div>
        </div>

        <!-- Report Context Information (Read-Only) -->
        <div class="divider"></div>

        <div class="context-section">
          <h3 class="context-title">
            <ion-icon
              :icon="informationCircleOutline"
              class="section-icon"
            ></ion-icon>
            Report Context
          </h3>
          <div class="context-grid">
            <div class="context-item">
              <ion-icon :icon="documentOutline" class="context-icon"></ion-icon>
              <div class="context-content">
                <label>Report ID</label>
                <span>#{{ report.id }}</span>
              </div>
            </div>

            <div class="context-item">
              <ion-icon :icon="personOutline" class="context-icon"></ion-icon>
              <div class="context-content">
                <label>Reporter</label>
                <span>{{ report.user?.name || "Unknown" }}</span>
              </div>
            </div>

            <div class="context-item">
              <ion-icon :icon="calendarOutline" class="context-icon"></ion-icon>
              <div class="context-content">
                <label>Created Date</label>
                <span>{{ formatDate(report.createdAt) }}</span>
              </div>
            </div>

            <div class="context-item">
              <ion-icon :icon="cubeOutline" class="context-icon"></ion-icon>
              <div class="context-content">
                <label>Items Count</label>
                <span>{{ report.items?.length || 0 }} item(s)</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Delete Button -->
        <div class="input-group">
          <ion-button
            fill="outline"
            color="danger"
            expand="block"
            @click="handleDelete"
          >
            <ion-icon :icon="trashOutline" slot="start"></ion-icon>
            Delete Report
          </ion-button>
        </div>
      </div>
    </div>
  </template-page>
</template>

<script setup lang="ts">
import {
  IonSelect,
  IonSelectOption,
  IonButton,
  IonSpinner,
  IonIcon,
} from "@ionic/vue";
import {
  alertCircleOutline,
  businessOutline,
  calendarOutline,
  checkmarkCircleOutline,
  closeCircleOutline,
  createOutline,
  cubeOutline,
  documentOutline,
  flagOutline,
  informationCircleOutline,
  locationOutline,
  lockClosedOutline,
  personOutline,
  refreshOutline,
  searchOutline,
  timeOutline,
  trashOutline,
  eyeOutline,
} from "ionicons/icons";
import TemplatePage from "@/components/TemplatePage.vue";
import { ref, computed, watch, onMounted, nextTick } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useReportStore } from "@/stores/reportStore";
import { useLocationStore } from "@/stores/locationStore";
import { useItemStore } from "@/stores/itemStore";
import type { Report } from "@/models/report";
import { ReportStatus, ReportType } from "@/models/report";
import { ItemStatus, type Item } from "@/models/item";
import type { Location } from "@/models/location";

const ReportStatusEnum = ReportStatus;

const router = useRouter();
const route = useRoute();
const reportStore = useReportStore();
const locationStore = useLocationStore();
const itemStore = useItemStore();

// Initialize with proper default values
const report = ref<Report>({
  id: 0,
  userId: 0,
  locationId: 0,
  createdAt: "",
  status: ReportStatusEnum.OPEN,
  type: ReportType.LOST,
});

const errors = ref({
  locationId: "",
  status: "",
});

const isLoading = ref(false);
const error = ref<string | null>(null);
const isSaving = ref(false);
const availableLocations = ref<Location[]>([]);

const leftFooterButton = computed(() => ({
  name: "Cancel",
  color: "medium",
  icon: closeCircleOutline,
}));

const rightFooterButton = computed(() => ({
  name: isSaving.value
    ? "Saving..."
    : isValid.value
      ? "Save Changes"
      : report.value.status === ReportStatusEnum.RESOLVED
        ? "Report is Resolved"
        : "Fill Required Fields",
  color:
    isValid.value && report.value.status !== ReportStatusEnum.RESOLVED
      ? "primary"
      : "medium",
  icon: checkmarkCircleOutline,
  disabled:
    !isValid.value ||
    isSaving.value ||
    isLoading.value ||
    report.value.status === ReportStatusEnum.RESOLVED,
}));

const isValid = computed(() => {
  return (
    report.value?.locationId > 0 &&
    report.value?.status !== undefined &&
    Object.values(errors.value).every(error => error === "")
  );
});

const canBeResolved = computed(() => {
  const items = report.value.items ?? [];
  return (
    items.length > 0 && items.every(item => item.status === ItemStatus.CLAIMED)
  );
});

const formatDate = (dateString: string) => {
  try {
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

const validateField = (fieldName: keyof typeof errors.value) => {
  try {
    switch (fieldName) {
      case "locationId":
        if (!report.value.locationId || report.value.locationId <= 0) {
          errors.value.locationId = "Location is required";
        } else {
          errors.value.locationId = "";
        }
        break;
      case "status":
        if (report.value.status === undefined || report.value.status === null) {
          errors.value.status = "Report status is required";
        } else {
          errors.value.status = "";
        }
        break;
    }
  } catch (error) {
    console.error("Error validating field:", fieldName, error);
  }
};

const validateAllFields = () => {
  try {
    validateField("locationId");
    validateField("status");
  } catch (error) {
    console.error("Error validating all fields:", error);
  }
};

const loadReport = async () => {
  try {
    isLoading.value = true;
    error.value = null;

    const reportId = Number(route.params.id);
    if (!reportId || isNaN(reportId)) {
      throw new Error("Invalid report ID");
    }

    await reportStore.fetchReportById(reportId);
    const loadedReport = reportStore.getCurrentReport;

    if (loadedReport) {
      await nextTick();
      report.value = { ...loadedReport };
    } else {
      throw new Error("Report not found");
    }
  } catch (err) {
    console.error("Error loading report:", err);
    error.value = err instanceof Error ? err.message : "Unknown error";
    setTimeout(() => {
      router.back();
    }, 2000);
  } finally {
    isLoading.value = false;
  }
};

const loadLocations = async () => {
  try {
    await locationStore.fetchLocations();
    availableLocations.value = locationStore.getLocations;
  } catch (err) {
    console.error("Error loading locations:", err);
  }
};

const handleCancel = () => {
  try {
    router.back();
  } catch (error) {
    console.error("Error navigating back:", error);
    router.push("/reports/overview");
  }
};

const handleSave = async () => {
  if (!isValid.value) {
    validateAllFields();
    return;
  }

  try {
    isSaving.value = true;

    const updateData = {
      locationId: report.value.locationId,
      status: report.value.status,
    };

    await reportStore.updateReport(report.value.id, updateData);

    // Navigate back to report details
    router.push(`/reports/${report.value.id}`);
  } catch (error) {
    console.error("Error saving report:", error);
    // You might want to show an error toast here
  } finally {
    isSaving.value = false;
  }
};

const handleDelete = () => {
  // just delete report and push user to reports overview
  reportStore
    .deleteReport(report.value.id)
    .then(() => {
      router.push("/reports/overview");
    })
    .catch(error => {
      console.error("Error deleting report:", error);
      // You might want to show an error toast here
    });
};

// Item management functions
const getItemStatusIcon = (status: ItemStatus) => {
  return status === ItemStatus.CLAIMED ? checkmarkCircleOutline : cubeOutline;
};

const getItemStatusClass = (status: ItemStatus) => {
  return status === ItemStatus.CLAIMED ? "status-claimed" : "status-unclaimed";
};

const getItemStatusText = (status: ItemStatus) => {
  return status === ItemStatus.CLAIMED ? "Claimed" : "Available";
};

const truncateText = (text: string, maxLength: number) => {
  if (!text) return "";
  return text.length > maxLength ? text.substring(0, maxLength) + "..." : text;
};

const navigateToItemDetails = (itemId: number) => {
  router.push(`/items/${itemId}`);
};

const navigateToEditItem = (itemId: number) => {
  router.push(`/items/${itemId}/edit`);
};

const confirmRemoveItem = async (item: Item) => {
  // Check if this is the last item in the report
  const remainingItemsCount = (report.value.items?.length || 0) - 1;

  let confirmMessage;
  if (remainingItemsCount === 0) {
    confirmMessage = `Are you sure you want to remove "${item.name}"? This is the last item in the report, so the entire report will be deleted. This action cannot be undone.`;
  } else {
    confirmMessage = `Are you sure you want to remove "${item.name}" from this report? This action cannot be undone.`;
  }

  const confirmed = confirm(confirmMessage);
  if (!confirmed) return;

  try {
    await itemStore.deleteItem(item.id);

    // If this was the last item, the backend automatically deletes the empty report
    if (remainingItemsCount === 0) {
      // Navigate to reports overview since the report no longer exists
      router.push("/reports/overview");
    } else {
      // Reload the report to update the items list
      await loadReport();
    }
  } catch (error) {
    console.error("Error removing item:", error);
    alert("Failed to remove item. Please try again.");
  }
};

// Watch for location changes to clear errors
watch(
  () => report.value?.locationId,
  newLocationId => {
    try {
      if (newLocationId && errors.value.locationId) {
        errors.value.locationId = "";
      }
    } catch (error) {
      console.error("Error in locationId watcher:", error);
    }
  }
);

watch(
  () => report.value?.status,
  newStatus => {
    try {
      if (newStatus !== undefined && errors.value.status) {
        errors.value.status = "";
      }
    } catch (error) {
      console.error("Error in status watcher:", error);
    }
  }
);

onMounted(async () => {
  await Promise.all([loadReport(), loadLocations()]);
});
</script>

<style scoped>
.form-container {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
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
  padding: 40px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  color: var(--ion-color-danger);
  margin-bottom: 16px;
}

.resolved-warning {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  margin-bottom: 24px;
  background: var(--ion-color-warning-tint);
  border: 1px solid var(--ion-color-warning);
  border-radius: 8px;
  color: var(--ion-color-warning-contrast);
}

.warning-icon {
  font-size: 24px;
  color: var(--ion-color-warning);
  flex-shrink: 0;
}

.warning-content h3 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--ion-color-warning-shade);
}

.warning-content p {
  margin: 0;
  font-size: 14px;
  color: var(--ion-color-warning-shade);
  line-height: 1.4;
}

.form-header {
  margin-bottom: 24px;
  text-align: center;
  margin-bottom: 40px;
}

.header-icon {
  font-size: 48px;
  color: var(--ion-color-primary);
  margin-bottom: 16px;
}

.form-header h2 {
  color: var(--ion-color-dark);
  margin: 0 0 8px 0;
  font-weight: 600;
}

.form-header p {
  color: var(--ion-color-medium);
  margin: 0 0 16px 0;
  font-size: 0.95em;
}

.last-modified {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  background: var(--ion-color-light-tint);
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.85em;
  color: var(--ion-color-medium);
  margin-top: 12px;
}

.time-icon {
  font-size: 14px;
}

.divider {
  height: 1px;
  background: var(--ion-color-light);
  margin: 24px 0 16px 0;
}

.form-content {
  padding: 0;
}

.input-group {
  margin-bottom: 24px;
}

.modern-select {
  --background: var(--ion-color-light-tint);
  --border-radius: 12px;
  --padding-start: 16px;
  --padding-end: 16px;
  --padding-top: 12px;
  --padding-bottom: 12px;
  border: 2px solid var(--ion-color-light-shade);
  border-radius: 12px;
  margin-bottom: 8px;
  transition: all 0.3s ease;
  position: relative;
}

.modern-select.select-filled {
  border-color: var(--ion-color-primary-tint);
  --background: rgba(var(--ion-color-primary-rgb), 0.05);
}

.modern-select:focus-within {
  border-color: var(--ion-color-primary);
  box-shadow: 0 0 0 3px rgba(var(--ion-color-primary-rgb), 0.1);
  transform: translateY(-2px);
}

.input-icon {
  color: var(--ion-color-medium);
  font-size: 18px;
}

.select-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.option-icon {
  font-size: 18px;
  color: var(--ion-color-primary);
}

.select-option div {
  flex: 1;
}

.select-option p {
  margin: 2px 0 0 0;
  font-size: 12px;
  color: var(--ion-color-medium);
}

.error-message {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--ion-color-danger);
  font-size: 0.85em;
  margin-top: 4px;
  padding-left: 4px;
  animation: shake 0.3s ease-in-out;
}

@keyframes shake {
  0%,
  100% {
    transform: translateX(0);
  }
  25% {
    transform: translateX(-5px);
  }
  75% {
    transform: translateX(5px);
  }
}

.context-section {
  margin: 24px 0;
  padding: 20px;
  background: var(--ion-color-light-tint);
  border-radius: 12px;
  border: 1px solid var(--ion-color-light);
}

.context-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 8px 0;
  color: var(--ion-color-dark);
  font-size: 16px;
  font-weight: 600;
}

.context-description {
  margin: 0 0 16px 0;
  color: var(--ion-color-medium);
  font-size: 14px;
}

.context-grid {
  display: grid;
  gap: 12px;
}

.context-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: var(--ion-item-background);
  border-radius: 8px;
  border: 1px solid var(--ion-color-light-shade);
}

.context-icon {
  color: var(--ion-color-medium);
  font-size: 18px;
  flex-shrink: 0;
}

.context-content {
  flex: 1;
}

.context-content label {
  display: block;
  font-size: 12px;
  color: var(--ion-color-medium);
  margin-bottom: 2px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.context-content span {
  font-size: 14px;
  color: var(--ion-color-dark);
  font-weight: 500;
}

/* Items Management Section */
.items-section {
  margin: 24px 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  color: var(--ion-color-dark);
  font-size: 16px;
  font-weight: 600;
}

.items-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 16px;
}

.item-card {
  background: var(--ion-item-background);
  border: 1px solid var(--ion-color-light-shade);
  border-radius: 8px;
  padding: 16px;
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

.item-info {
  flex: 1;
}

.item-name {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--ion-color-dark);
}

.item-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
}

.status-icon {
  font-size: 16px;
}

.status-claimed {
  color: var(--ion-color-success);
}

.status-unclaimed {
  color: var(--ion-color-warning);
}

.item-actions {
  display: flex;
  gap: 4px;
}

.item-actions ion-button {
  --padding-start: 8px;
  --padding-end: 8px;
  --padding-top: 8px;
  --padding-bottom: 8px;
  min-height: 32px;
  min-width: 32px;
}

.item-actions ion-button:hover {
  transform: scale(1.1);
  transition: transform 0.2s ease;
}

.item-description {
  margin: 0;
  font-size: 14px;
  color: var(--ion-color-medium);
  line-height: 1.4;
}

.empty-items {
  text-align: center;
  padding: 40px 20px;
  background: var(--ion-color-light-tint);
  border-radius: 8px;
  border: 2px dashed var(--ion-color-light-shade);
}

.empty-icon {
  font-size: 48px;
  color: var(--ion-color-medium);
  margin-bottom: 16px;
}

.empty-items h4 {
  margin: 0 0 8px 0;
  color: var(--ion-color-dark);
}

.empty-items p {
  margin: 0 0 16px 0;
  color: var(--ion-color-medium);
  font-size: 14px;
}

@media (max-width: 768px) {
  .form-container {
    padding: 16px;
  }

  .form-header {
    margin-bottom: 30px;
  }

  .header-icon {
    font-size: 40px;
  }

  .last-modified {
    padding: 6px 12px;
    font-size: 0.8em;
  }

  .section-header {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .item-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .item-actions {
    justify-content: flex-start;
  }
}

@media (max-width: 480px) {
  .form-header {
    margin-bottom: 20px;
  }

  .input-group {
    margin-bottom: 20px;
  }

  .items-section {
    margin: 20px 0;
  }

  .empty-items {
    padding: 30px 16px;
  }
}
</style>
