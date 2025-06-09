<template>
  <template-page
    :headline="'Report Item'"
    :leftFooterButton="leftFooterButton"
    :rightFooterButton="rightFooterButton"
    @leftFooterButtonClicked="handleCancel"
    @rightFooterButtonClicked="handleSubmit"
  >
    <div class="form-container">
      <div class="progress-section">
        <div class="progress-bar">
          <div
            class="progress-fill"
            :style="{ width: `${completionPercentage}%` }"
          ></div>
        </div>
        <p class="progress-text">{{ completionPercentage }}% completed</p>
      </div>

      <div class="form-header">
        <ion-icon :icon="megaphoneOutline" class="header-icon"></ion-icon>
        <h2>Create New Report</h2>
        <p>Help others find lost items or report a found item</p>
      </div>

      <div class="form-content">
        <div class="input-group">
          <ion-select
            v-model="reportData.type"
            label="Report Type *"
            label-placement="stacked"
            placeholder="Choose an option"
            interface="popover"
            class="modern-select"
            :class="{
              'select-filled': reportData.type,
              'select-error': errors.type,
            }"
            @selectionChange="validateField('type')"
          >
            <ion-select-option value="LOST">
              <div class="select-option">
                <ion-icon :icon="searchOutline" class="option-icon"></ion-icon>
                <div>
                  <strong>Lost Report</strong>
                  <p>I have lost an item</p>
                </div>
              </div>
            </ion-select-option>
            <ion-select-option value="FOUND">
              <div class="select-option">
                <ion-icon :icon="eyeOutline" class="option-icon"></ion-icon>
                <div>
                  <strong>Found Report</strong>
                  <p>I have found an item</p>
                </div>
              </div>
            </ion-select-option>
          </ion-select>
          <div v-if="errors.type" class="error-message">
            <ion-icon :icon="alertCircleOutline"></ion-icon>
            {{ errors.type }}
          </div>
        </div>

        <div class="input-group">
          <ion-input
            v-model="reportData.itemName"
            label="Item Name *"
            label-placement="stacked"
            placeholder="e.g. iPhone 14, Key Chain, Backpack..."
            class="modern-input"
            :class="{
              'input-filled': reportData.itemName,
              'input-error': errors.itemName,
            }"
            @ionBlur="validateField('itemName')"
          ></ion-input>
          <div v-if="errors.itemName" class="error-message">
            <ion-icon :icon="alertCircleOutline"></ion-icon>
            {{ errors.itemName }}
          </div>
        </div>

        <div class="input-group">
          <ion-textarea
            v-model="reportData.description"
            label="Description"
            label-placement="stacked"
            placeholder="Describe the item in detail: color, size, special features, where you lost/found it..."
            class="modern-textarea"
            :class="{
              'textarea-filled': reportData.description,
            }"
            :rows="4"
          ></ion-textarea>
        </div>

        <div class="input-group">
          <ion-select
            v-model="reportData.location"
            label="Location *"
            label-placement="stacked"
            placeholder="Choose a location"
            interface="popover"
            class="modern-select"
            :class="{
              'select-filled': reportData.location,
              'select-error': errors.location,
            }"
            @selectionChange="validateField('location')"
          >
            <ion-select-option
              v-for="location in availableLocations"
              :key="location.id"
              :value="location.name"
            >
              {{ location.name }}
            </ion-select-option>
            <ion-select-option value="other">
              <strong>Other Location (please specify in description)</strong>
            </ion-select-option>
          </ion-select>
          <div v-if="errors.location" class="error-message">
            <ion-icon :icon="alertCircleOutline"></ion-icon>
            {{ errors.location }}
          </div>
        </div>

        <div class="input-group">
          <h3 class="section-title">
            <ion-icon :icon="personOutline" class="section-icon"></ion-icon>
            Your Contact Information
          </h3>
          <p class="section-description">
            So others can contact you if they have found your item.
          </p>

          <ion-input
            v-model="reportData.reporterName"
            label="Your Name *"
            label-placement="stacked"
            placeholder="First and Last Name"
            class="modern-input"
            :class="{
              'input-filled': reportData.reporterName,
              'input-error': errors.reporterName,
            }"
            @ionBlur="validateField('reporterName')"
          ></ion-input>
          <div v-if="errors.reporterName" class="error-message">
            <ion-icon :icon="alertCircleOutline"></ion-icon>
            {{ errors.reporterName }}
          </div>

          <ion-input
            v-model="reportData.contactInfo"
            label="Contact (optional)"
            label-placement="stacked"
            placeholder="Email or Phone"
            class="modern-input"
            :class="{
              'input-filled': reportData.contactInfo,
            }"
          ></ion-input>
        </div>

        <div class="input-group">
          <div class="image-upload-section">
            <h3 class="upload-title">
              <ion-icon :icon="cameraOutline" class="title-icon"></ion-icon>
              Add Photo (optional)
            </h3>
            <p class="upload-description">
              A photo helps others identify the item
            </p>

            <div v-if="imagePreview" class="image-preview-container">
              <img :src="imagePreview" alt="Preview" class="image-preview" />
              <ion-button
                fill="clear"
                color="danger"
                class="remove-image-btn"
                @click="removeImage"
              >
                <ion-icon :icon="trashOutline" slot="icon-only"></ion-icon>
              </ion-button>
            </div>

            <div v-else class="upload-buttons">
              <ion-button
                expand="block"
                fill="outline"
                class="upload-btn"
                @click="takePhoto"
              >
                <ion-icon :icon="cameraOutline" slot="start"></ion-icon>
                Take Photo
              </ion-button>
              <ion-button
                expand="block"
                fill="outline"
                class="upload-btn"
                @click="triggerFileInput"
              >
                <ion-icon :icon="cloudUploadOutline" slot="start"></ion-icon>
                Upload Photo
              </ion-button>
            </div>

            <input
              ref="fileInput"
              type="file"
              accept="image/*"
              style="display: none"
              @change="handleFileSelect"
            />
          </div>
        </div>

        <div class="tips-section">
          <h3 class="tips-title">
            <ion-icon :icon="bulbOutline" class="title-icon"></ion-icon>
            Tips for a Good Report
          </h3>
          <div class="tips-list">
            <div class="tip-item">
              <ion-icon
                :icon="checkmarkCircleOutline"
                color="success"
              ></ion-icon>
              <div class="tip-text">
                <p>
                  <strong>Be detailed:</strong> The more details, the better
                  others can help
                </p>
              </div>
            </div>
            <div class="tip-item">
              <ion-icon
                :icon="checkmarkCircleOutline"
                color="success"
              ></ion-icon>
              <div class="tip-text">
                <p>
                  <strong>Add a photo:</strong> Images help enormously with
                  identification
                </p>
              </div>
            </div>
            <div class="tip-item">
              <ion-icon
                :icon="checkmarkCircleOutline"
                color="success"
              ></ion-icon>
              <div class="tip-text">
                <p>
                  <strong>Choose the right location:</strong> Where did you last
                  see the item?
                </p>
              </div>
            </div>
            <div class="tip-item">
              <ion-icon
                :icon="checkmarkCircleOutline"
                color="success"
              ></ion-icon>
              <div class="tip-text">
                <p>
                  <strong>Stay reachable:</strong> Provide correct contact
                  information
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template-page>
</template>

<script setup lang="ts">
import TemplatePage from "@/components/TemplatePage.vue";
import {
  IonInput,
  IonTextarea,
  IonIcon,
  IonSelect,
  IonSelectOption,
  IonButton,
} from "@ionic/vue";
import {
  megaphoneOutline,
  alertCircleOutline,
  checkmarkCircleOutline,
  closeCircleOutline,
  cameraOutline,
  cloudUploadOutline,
  trashOutline,
  bulbOutline,
  personOutline,
  searchOutline,
  eyeOutline,
} from "ionicons/icons";
import { ref, computed, watch, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useItemStore } from "@/stores/itemStore";
import { useLocationStore } from "@/stores/locationStore";
import { useReportStore } from "@/stores/reportStore";
import type { Item } from "@/models/item";
import type { ReportCreateData } from "@/models/report";

const router = useRouter();
const itemStore = useItemStore();
const locationStore = useLocationStore();
const reportStore = useReportStore();

// Mock user ID for now - in a real app, this would come from authentication
const CURRENT_USER_ID = 1;

const reportData = ref({
  type: "",
  itemName: "",
  description: "",
  location: "",
  reporterName: "",
  contactInfo: "",
  imageData: "",
});

const errors = ref({
  type: "",
  itemName: "",
  location: "",
  reporterName: "",
});

const isSubmitting = computed(() => itemStore.isLoading);
const imagePreview = ref("");
const fileInput = ref<HTMLInputElement>();
const availableLocations = computed(() => locationStore.getLocations || []);

const leftFooterButton = computed(() => ({
  name: "Cancel",
  color: "medium",
  icon: closeCircleOutline,
}));

const rightFooterButton = computed(() => ({
  name: isSubmitting.value
    ? "Submitting Report..."
    : isValid.value
      ? "Submit Report"
      : "Fill Required Fields",
  color: isValid.value ? "primary" : "medium",
  icon: checkmarkCircleOutline,
  disabled: !isValid.value || isSubmitting.value,
}));

const isValid = computed(() => {
  return (
    reportData.value.type.trim() !== "" &&
    reportData.value.itemName.trim() !== "" &&
    reportData.value.location.trim() !== "" &&
    reportData.value.reporterName.trim() !== ""
  );
});

const completionPercentage = computed(() => {
  const requiredFields = ["type", "itemName", "location", "reporterName"];
  const optionalFields = ["description", "contactInfo"];

  const requiredFilled = requiredFields.filter(
    field =>
      reportData.value[field as keyof typeof reportData.value].trim() !== ""
  ).length;

  const optionalFilled = optionalFields.filter(
    field =>
      reportData.value[field as keyof typeof reportData.value].trim() !== ""
  ).length;

  const requiredWeight = 0.8;
  const optionalWeight = 0.2;

  const requiredScore =
    (requiredFilled / requiredFields.length) * requiredWeight;
  const optionalScore =
    (optionalFilled / optionalFields.length) * optionalWeight;

  return Math.round((requiredScore + optionalScore) * 100);
});

onMounted(async () => {
  await locationStore.fetchLocations();
});

const validateField = (fieldName: keyof typeof errors.value) => {
  const value = String(
    reportData.value[fieldName as keyof typeof reportData.value] || ""
  ).trim();

  switch (fieldName) {
    case "type":
      errors.value.type = !value ? "Please select the report type" : "";
      break;
    case "itemName":
      if (!value) {
        errors.value.itemName = "Item name is required";
      } else if (value.length < 2) {
        errors.value.itemName = "Name must be at least 2 characters long";
      } else {
        errors.value.itemName = "";
      }
      break;
    case "location":
      errors.value.location = !value ? "Please select a location" : "";
      break;
    case "reporterName":
      if (!value) {
        errors.value.reporterName = "Your name is required";
      } else if (value.length < 2) {
        errors.value.reporterName = "Name must be at least 2 characters long";
      } else {
        errors.value.reporterName = "";
      }
      break;
  }
};

const validateAllFields = () => {
  validateField("type");
  validateField("itemName");
  validateField("location");
  validateField("reporterName");
};

const handleCancel = () => {
  router.back();
};

const handleSubmit = async () => {
  if (!isValid.value) {
    validateAllFields();
    return;
  }

  try {
    const selectedLocation = availableLocations.value.find(
      location => location.name === reportData.value.location
    );

    if (!selectedLocation) {
      console.error("Selected location not found");
      return;
    }

    const reportCreateData: ReportCreateData = {
      userId: CURRENT_USER_ID,
      locationId: selectedLocation.id,
      status: reportData.value.type === "FOUND", // true for FOUND, false for LOST
    };

    const newReport = await reportStore.createReport(reportCreateData);

    if (!newReport) {
      console.error("Failed to create report");
      return;
    }

    const enhancedDescription = [
      reportData.value.description,
      "",
      `--- Report Information ---`,
      `Report Type: ${
        reportData.value.type === "LOST" ? "Lost Report" : "Found Report"
      }`,
      `Reported by: ${reportData.value.reporterName}`,
      reportData.value.contactInfo
        ? `Contact: ${reportData.value.contactInfo}`
        : "",
      `Report Date: ${new Date().toLocaleDateString("en-US")}`,
    ]
      .filter(Boolean)
      .join("\n");

    const itemData: Omit<Item, "id" | "createdAt"> = {
      name: reportData.value.itemName,
      description: enhancedDescription,
      reportId: newReport.id,
    };

    const newItem = await itemStore.createItem(itemData);

    if (newItem) {
      console.log("Report submitted successfully:", newItem);
      router.push(`/items/${newItem.id}`);
    }
  } catch (error) {
    console.error("Error submitting report:", error);
  }
};

const triggerFileInput = () => {
  fileInput.value?.click();
};

const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];

  if (file) {
    if (!file.type.startsWith("image/")) {
      console.error("Please select an image file");
      return;
    }

    const maxSize = 5 * 1024 * 1024; // 5MB
    if (file.size > maxSize) {
      console.error("File size must be less than 5MB");
      return;
    }

    const reader = new FileReader();
    reader.onload = e => {
      const result = e.target?.result as string;
      imagePreview.value = result;
      reportData.value.imageData = result;
    };
    reader.readAsDataURL(file);
  }
};

const takePhoto = async () => {
  try {
    triggerFileInput();
  } catch (error) {
    console.error("Error taking photo:", error);
  }
};

const removeImage = () => {
  imagePreview.value = "";
  reportData.value.imageData = "";
  if (fileInput.value) {
    fileInput.value.value = "";
  }
};

watch(
  () => reportData.value.type,
  () => {
    if (errors.value.type) validateField("type");
  }
);

watch(
  () => reportData.value.itemName,
  () => {
    if (errors.value.itemName) validateField("itemName");
  }
);

watch(
  () => reportData.value.location,
  () => {
    if (errors.value.location) validateField("location");
  }
);

watch(
  () => reportData.value.reporterName,
  () => {
    if (errors.value.reporterName) validateField("reporterName");
  }
);
</script>

<style scoped>
.form-container {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}

.progress-section {
  margin-bottom: 30px;
  text-align: center;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background: var(--ion-color-light-shade);
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 8px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(
    90deg,
    var(--ion-color-primary),
    var(--ion-color-primary-shade)
  );
  border-radius: 4px;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 0.85em;
  color: var(--ion-color-medium);
  font-weight: 600;
  margin: 0;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.header-icon {
  font-size: 56px;
  color: var(--ion-color-primary);
  margin-bottom: 16px;
}

.form-header h2 {
  color: var(--ion-color-dark);
  margin: 0 0 8px 0;
  font-weight: 700;
  font-size: 1.8em;
}

.form-header p {
  color: var(--ion-color-medium);
  margin: 0 0 24px 0;
  font-size: 1em;
  line-height: 1.4;
}

.form-content {
  animation: slideInUp 0.6s ease-out;
}

.input-group {
  margin-bottom: 24px;
}

.section-title {
  color: var(--ion-color-dark);
  margin: 0 0 8px 0;
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

.section-description {
  color: var(--ion-color-medium);
  margin: 0 0 16px 0;
  font-size: 0.9em;
  line-height: 1.4;
}

.modern-select,
.modern-input,
.modern-textarea {
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
}

.modern-select.select-filled,
.modern-input.input-filled,
.modern-textarea.textarea-filled {
  border-color: var(--ion-color-primary-tint);
  --background: rgba(var(--ion-color-primary-rgb), 0.05);
}

.modern-select:focus-within,
.modern-input:focus-within,
.modern-textarea:focus-within {
  border-color: var(--ion-color-primary);
  box-shadow: 0 0 0 3px rgba(var(--ion-color-primary-rgb), 0.1);
  transform: translateY(-2px);
}

.modern-textarea {
  min-height: 80px;
  resize: vertical;
}

.select-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.option-icon {
  font-size: 20px;
  color: var(--ion-color-primary);
}

.select-option div {
  flex: 1;
}

.select-option p {
  margin: 2px 0 0 0;
  font-size: 0.85em;
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

.image-upload-section {
  background: var(--ion-color-light-tint);
  border: 2px dashed var(--ion-color-light-shade);
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  transition: all 0.3s ease;
}

.image-upload-section:hover {
  border-color: var(--ion-color-primary-tint);
  background: rgba(var(--ion-color-primary-rgb), 0.02);
}

.upload-title {
  color: var(--ion-color-dark);
  margin: 0 0 8px 0;
  font-size: 1.1em;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.title-icon {
  font-size: 20px;
  color: var(--ion-color-primary);
}

.upload-description {
  color: var(--ion-color-medium);
  margin: 0 0 20px 0;
  font-size: 0.9em;
}

.image-preview-container {
  position: relative;
  display: inline-block;
  margin-bottom: 16px;
}

.image-preview {
  max-width: 200px;
  max-height: 200px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.remove-image-btn {
  position: absolute;
  top: -8px;
  right: -8px;
  --background: var(--ion-color-danger);
  --color: white;
  --border-radius: 50%;
  width: 32px;
  height: 32px;
}

.upload-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-width: 300px;
  margin: 0 auto;
}

.upload-btn {
  --border-width: 2px;
  --border-style: solid;
  font-weight: 500;
}

.tips-section {
  background: var(--ion-color-light-tint);
  border-radius: 12px;
  padding: 20px;
  margin-top: 32px;
  border: 1px solid var(--ion-color-light-shade);
}

.tips-title {
  color: var(--ion-color-dark);
  margin: 0 0 16px 0;
  font-size: 1.1em;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tip-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  background: white;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid var(--ion-color-light);
}

.tip-text p {
  margin: 0;
  color: var(--ion-color-dark);
  font-size: 0.9em;
  line-height: 1.4;
}

.tip-text strong {
  color: var(--ion-color-dark-shade);
  font-weight: 600;
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

@media (max-width: 768px) {
  .form-container {
    padding: 16px;
  }

  .form-header {
    margin-bottom: 32px;
  }

  .header-icon {
    font-size: 48px;
  }

  .form-header h2 {
    font-size: 1.6em;
  }

  .upload-buttons {
    max-width: 100%;
  }
}

@media (max-width: 480px) {
  .progress-section {
    margin-bottom: 24px;
  }

  .form-header {
    margin-bottom: 24px;
  }

  .input-group {
    margin-bottom: 20px;
  }

  .tips-section {
    margin-top: 24px;
    padding: 16px;
  }
}
</style>
