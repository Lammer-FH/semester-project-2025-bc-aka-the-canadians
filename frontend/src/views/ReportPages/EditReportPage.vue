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
        <div class="form-header">
          <ion-icon :icon="createOutline" class="header-icon"></ion-icon>
          <h2>Edit Report #{{ report.id }}</h2>
          <p>Update the report details</p>
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
          <p class="section-description">These report-specific fields can be edited:</p>
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
                <ion-icon :icon="businessOutline" class="option-icon"></ion-icon>
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
            label="Report Type *"
            label-placement="stacked"
            placeholder="Choose report type"
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
            <ion-select-option :value="false">
              <div class="select-option">
                <ion-icon :icon="searchOutline" class="option-icon"></ion-icon>
                <div>
                  <strong>Lost Item Report</strong>
                  <p>I lost an item</p>
                </div>
              </div>
            </ion-select-option>
            <ion-select-option :value="true">
              <div class="select-option">
                <ion-icon :icon="eyeOutline" class="option-icon"></ion-icon>
                <div>
                  <strong>Found Item Report</strong>
                  <p>I found an item</p>
                </div>
              </div>
            </ion-select-option>
          </ion-select>
          <div v-if="errors.status" class="error-message">
            <ion-icon :icon="alertCircleOutline"></ion-icon>
            {{ errors.status }}
          </div>
        </div>

        <!-- Report Context Information (Read-Only) -->
        <div class="divider"></div>
        
        <div class="context-section">
          <h3 class="context-title">
            <ion-icon :icon="informationCircleOutline" class="section-icon"></ion-icon>
            Report Context (Read-Only)
          </h3>
          <p class="context-description">This information cannot be edited:</p>
          
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
                <span>{{ report.user?.name || 'Unknown' }}</span>
              </div>
            </div>

            <div class="context-item">
              <ion-icon :icon="calendarOutline" class="context-icon"></ion-icon>
              <div class="context-content">
                <label>Created Date</label>
                <span>{{ formatDate(report.createdAt) }}</span>
              </div>
            </div>

            <div v-if="report.items && report.items.length > 0" class="context-item">
              <ion-icon :icon="cubeOutline" class="context-icon"></ion-icon>
              <div class="context-content">
                <label>Items Count</label>
                <span>{{ report.items.length }} item(s)</span>
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

      <!-- Delete Alert -->
      <ion-alert
        :is-open="showDeleteAlert"
        header="Delete Report"
        :message="`Are you sure you want to delete Report #${report.id}? This will also delete all associated items. This action cannot be undone.`"
        :buttons="alertButtons"
        @didDismiss="showDeleteAlert = false"
      ></ion-alert>
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
  IonAlert,
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
  eyeOutline,
  flagOutline,
  informationCircleOutline,
  locationOutline,
  personOutline,
  refreshOutline,
  searchOutline,
  timeOutline,
  trashOutline,
} from "ionicons/icons";
import TemplatePage from "@/components/TemplatePage.vue";
import { ref, computed, watch, onMounted, nextTick } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useReportStore } from "@/stores/reportStore";
import { useLocationStore } from "@/stores/locationStore";
import type { Report } from "@/models/report";
import type { Location } from "@/models/location";

const router = useRouter();
const route = useRoute();
const reportStore = useReportStore();
const locationStore = useLocationStore();

// Initialize with proper default values
const report = ref<Report>({
  id: 0,
  userId: 0,
  locationId: 0,
  createdAt: "",
  status: false,
});

const errors = ref({
  locationId: "",
  status: "",
});

const isLoading = ref(false);
const error = ref<string | null>(null);
const isSaving = ref(false);
const showDeleteAlert = ref(false);
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
      : "Fill Required Fields",
  color: isValid.value ? "primary" : "medium",
  icon: checkmarkCircleOutline,
  disabled: !isValid.value || isSaving.value || isLoading.value,
}));

const isValid = computed(() => {
  return (
    report.value?.locationId > 0 &&
    report.value?.status !== undefined &&
    Object.values(errors.value).every(error => error === "")
  );
});

const alertButtons = [
  {
    text: "Cancel",
    role: "cancel",
    cssClass: "alert-button-cancel",
  },
  {
    text: "Delete",
    role: "destructive",
    cssClass: "alert-button-confirm",
    handler: () => confirmDelete(),
  },
];

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
          errors.value.status = "Report type is required";
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
  showDeleteAlert.value = true;
};

const confirmDelete = async () => {
  try {
    await reportStore.deleteReport(report.value.id);
    router.push("/reports/overview");
  } catch (error) {
    console.error("Error deleting report:", error);
    // You might want to show an error toast here
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
  padding: 16px;
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

.form-header {
  margin-bottom: 24px;
  text-align: center;
}

.header-icon {
  font-size: 32px;
  color: var(--ion-color-primary);
  margin-bottom: 8px;
}

.form-header h2 {
  margin: 8px 0 4px 0;
  color: var(--ion-color-dark);
}

.form-header p {
  margin: 0 0 16px 0;
  color: var(--ion-color-medium);
}

.last-modified {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
  color: var(--ion-color-medium);
  margin-top: 8px;
}

.time-icon {
  font-size: 16px;
}

.editable-section {
  margin-bottom: 16px;
  padding: 16px 0 8px 0;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 6px 0;
  color: var(--ion-color-success);
  font-size: 16px;
  font-weight: 600;
}

.section-description {
  margin: 0;
  color: var(--ion-color-medium);
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
  margin-bottom: 16px;
}

.modern-select {
  --border-radius: 12px;
  --border-color: var(--ion-color-light);
  --border-style: solid;
  --border-width: 1px;
  --padding-start: 44px;
  --padding-end: 16px;
  --padding-top: 16px;
  --padding-bottom: 16px;
  --background: var(--ion-color-light-tint);
  margin-bottom: 8px;
}

.modern-select.select-filled {
  --border-color: var(--ion-color-primary);
  --color: var(--ion-color-dark);
  --background: var(--ion-item-background);
}

.modern-select:focus-within {
  --border-color: var(--ion-color-primary);
  --border-width: 2px;
  --background: var(--ion-item-background);
}

.input-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--ion-color-medium);
  font-size: 18px;
  z-index: 10;
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
  gap: 8px;
  color: var(--ion-color-danger);
  font-size: 14px;
  margin-top: 4px;
  padding: 0 4px;
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

@media (max-width: 768px) {
  .form-container {
    padding: 12px;
  }

  .form-header {
    margin-bottom: 20px;
  }

  .header-icon {
    font-size: 28px;
  }

  .last-modified {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .form-header {
    margin-bottom: 16px;
  }

  .input-group {
    margin-bottom: 12px;
  }
}
</style>