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
            <ion-select-option :value="ReportType.LOST">
              <div class="select-option">
                <ion-icon :icon="searchOutline" class="option-icon"></ion-icon>
                <div>
                  <p>I have lost an item(s)</p>
                </div>
              </div>
            </ion-select-option>
            <ion-select-option :value="ReportType.FOUND">
              <div class="select-option">
                <ion-icon :icon="eyeOutline" class="option-icon"></ion-icon>
                <div>
                  <p>I have found an item(s)</p>
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
          <h3 class="section-title">
            <ion-icon :icon="cubeOutline" class="section-icon"></ion-icon>
            Items to Report
          </h3>
          <p class="section-description">
            Add one or more items to this report. You can report multiple items
            found or lost together.
          </p>

          <div class="items-section">
            <div
              v-for="(item, index) in reportData.items"
              :key="index"
              class="item-card"
            >
              <div class="item-header">
                <h4>Item {{ index + 1 }}</h4>
                <ion-button
                  v-if="reportData.items.length > 1"
                  fill="clear"
                  color="danger"
                  size="small"
                  @click="removeItem(index)"
                >
                  <ion-icon :icon="trashOutline" slot="icon-only"></ion-icon>
                </ion-button>
              </div>

              <div class="item-inputs">
                <ion-input
                  v-model="item.name"
                  label="Item Name *"
                  label-placement="stacked"
                  placeholder="e.g. iPhone 14, Key Chain, Backpack..."
                  class="modern-input"
                  :class="{
                    'input-filled': item.name,
                    'input-error': errors.items[index]?.name,
                  }"
                  @ionBlur="validateItemField(index, 'name')"
                ></ion-input>
                <div v-if="errors.items[index]?.name" class="error-message">
                  <ion-icon :icon="alertCircleOutline"></ion-icon>
                  {{ errors.items[index].name }}
                </div>

                <ion-textarea
                  v-model="item.description"
                  label="Description *"
                  label-placement="stacked"
                  placeholder="Describe the item in detail: color, size, special features..."
                  class="modern-textarea"
                  :class="{
                    'textarea-filled': item.description,
                    'input-error': errors.items[index]?.description,
                  }"
                  :rows="3"
                  @ionBlur="validateItemField(index, 'description')"
                ></ion-textarea>
                <div
                  v-if="errors.items[index]?.description"
                  class="error-message"
                >
                  <ion-icon :icon="alertCircleOutline"></ion-icon>
                  {{ errors.items[index].description }}
                </div>
              </div>
            </div>

            <ion-button
              fill="outline"
              color="primary"
              @click="addItem"
              class="add-item-button"
            >
              <ion-icon :icon="addOutline" slot="start"></ion-icon>
              Add Another Item
            </ion-button>
          </div>
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
          </ion-select>
          <div v-if="errors.location" class="error-message">
            <ion-icon :icon="alertCircleOutline"></ion-icon>
            {{ errors.location }}
          </div>
        </div>

        <div v-if="!currentUser" class="input-group">
          <h3 class="section-title">
            <ion-icon :icon="personOutline" class="section-icon"></ion-icon>
            Your Contact Information
          </h3>
          <p class="section-description">
            So others can contact you if they have found your item. This will
            automatically create a user profile so that next time you can report
            items faster.
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
            label="Email *"
            label-placement="stacked"
            placeholder="Enter your email address"
            class="modern-input"
            :class="{
              'input-filled': reportData.contactInfo,
              'input-error': errors.contactInfo,
            }"
            @ionBlur="validateField('contactInfo')"
          ></ion-input>
          <div v-if="errors.contactInfo" class="error-message">
            <ion-icon :icon="alertCircleOutline"></ion-icon>
            {{ errors.contactInfo }}
          </div>
        </div>

        <div v-else class="input-group">
          <h3 class="section-title">
            <ion-icon :icon="personOutline" class="section-icon"></ion-icon>
            Reporting as
          </h3>
          <p class="section-description">
            You're logged in as {{ currentUser.name }} ({{
              currentUser.email
            }}). This report will be associated with your profile.
          </p>
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
  personOutline,
  searchOutline,
  eyeOutline,
  cubeOutline,
  addOutline,
  trashOutline,
} from "ionicons/icons";
import { ref, computed, watch, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useItemStore } from "@/stores/itemStore";
import { useLocationStore } from "@/stores/locationStore";
import { useReportStore } from "@/stores/reportStore";
import { useUserStore } from "@/stores/userStore";
import {
  ReportType,
  type ReportCreateData,
  type ReportItemCreateData,
} from "@/models/report";

const router = useRouter();
const itemStore = useItemStore();
const locationStore = useLocationStore();
const reportStore = useReportStore();
const userStore = useUserStore();

const reportData = ref({
  type: null as ReportType | null,
  location: "",
  reporterName: "",
  contactInfo: "",
  items: [
    {
      name: "",
      description: "",
    },
  ] as ReportItemCreateData[],
});

const errors = ref({
  type: "",
  location: "",
  reporterName: "",
  contactInfo: "",
  items: [
    {
      name: "",
      description: "",
    },
  ] as Array<{ name: string; description: string }>,
});

const isSubmitting = computed(() => itemStore.isLoading);
const currentUser = computed(() => userStore.getCurrentUser);
const availableLocations = computed(() => locationStore.getLocations || []);

const leftFooterButton = computed(() => ({
  name: "Cancel",
  color: "medium",
  icon: closeCircleOutline,
}));

const rightFooterButton = computed(() => ({
  name: "Submit Report",
  color: isValid.value && !isSubmitting.value ? "primary" : "medium",
  icon: checkmarkCircleOutline,
  disabled: !isValid.value || isSubmitting.value,
}));

const isValid = computed(() => {
  const baseValid =
    reportData.value.type !== null &&
    typeof reportData.value.location === "string" &&
    reportData.value.location.trim() !== "" &&
    reportData.value.items.length > 0 &&
    reportData.value.items.every(
      item =>
        item.name.trim() !== "" &&
        item.description &&
        item.description.trim() !== ""
    );

  // If there's a current user, we don't need contact info
  if (currentUser.value) {
    return baseValid;
  }

  // If no current user, require contact info
  return (
    baseValid &&
    typeof reportData.value.reporterName === "string" &&
    reportData.value.reporterName.trim() !== "" &&
    typeof reportData.value.contactInfo === "string" &&
    validateEmail(reportData.value.contactInfo)
  );
});

const completionPercentage = computed(() => {
  const baseFields = ["type", "location"];
  const contactFields = currentUser.value
    ? []
    : ["reporterName", "contactInfo"];
  const requiredFields = [...baseFields, ...contactFields];

  let totalFields = requiredFields.length + reportData.value.items.length * 2; // name and description per item
  let filledFields = requiredFields.filter(field => {
    const value = reportData.value[field as keyof typeof reportData.value];
    if (field === "type") {
      return value !== null;
    }
    if (field === "contactInfo") {
      return typeof value === "string" && validateEmail(value);
    }
    return typeof value === "string" && value.trim() !== "";
  }).length;

  // Add item fields
  reportData.value.items.forEach(item => {
    if (item.name.trim() !== "") filledFields++;
    if (item.description && item.description.trim() !== "") filledFields++;
  });

  return Math.round((filledFields / totalFields) * 100);
});

onMounted(async () => {
  await locationStore.fetchLocations();
});

const validateEmail = (email: string) => {
  // Simple email regex
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
};

// Item management functions
const addItem = () => {
  reportData.value.items.push({
    name: "",
    description: "",
  });
  errors.value.items.push({
    name: "",
    description: "",
  });
};

const removeItem = (index: number) => {
  if (reportData.value.items.length > 1) {
    reportData.value.items.splice(index, 1);
    errors.value.items.splice(index, 1);
  }
};

const validateItemField = (
  itemIndex: number,
  fieldName: "name" | "description"
) => {
  const item = reportData.value.items[itemIndex];
  const value = item[fieldName] || "";

  switch (fieldName) {
    case "name":
      if (!value.trim()) {
        errors.value.items[itemIndex].name = "Item name is required";
      } else if (value.trim().length < 2) {
        errors.value.items[itemIndex].name =
          "Name must be at least 2 characters long";
      } else {
        errors.value.items[itemIndex].name = "";
      }
      break;
    case "description":
      errors.value.items[itemIndex].description = !value.trim()
        ? "Description is required"
        : "";
      break;
  }
};

const validateField = (fieldName: keyof typeof errors.value) => {
  const value = String(
    reportData.value[fieldName as keyof typeof reportData.value] || ""
  ).trim();

  switch (fieldName) {
    case "type":
      errors.value.type = !reportData.value.type
        ? "Please select the report type"
        : "";
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
    case "contactInfo":
      if (!value) {
        errors.value.contactInfo = "Email is required";
      } else if (!validateEmail(value)) {
        errors.value.contactInfo = "Please enter a valid email address";
      } else {
        errors.value.contactInfo = "";
      }
      break;
  }
};

const validateAllFields = () => {
  validateField("type");
  validateField("location");

  // Validate all items
  reportData.value.items.forEach((_, index) => {
    validateItemField(index, "name");
    validateItemField(index, "description");
  });

  // Only validate contact fields if no current user
  if (!currentUser.value) {
    validateField("reporterName");
    validateField("contactInfo");
  }
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
    let user;

    if (currentUser.value) {
      // Use existing current user
      user = currentUser.value;
    } else {
      // Create or get user from contact information
      try {
        user = await userStore.createUserFromContactInfo(
          reportData.value.reporterName,
          reportData.value.contactInfo
        );

        if (!user) {
          console.error("Failed to create or get user");
          return;
        }
      } catch (userError) {
        console.error("Error creating user:", userError);

        // Check if it's an email uniqueness error
        if (
          userError instanceof Error &&
          userError.message.toLowerCase().includes("email")
        ) {
          errors.value.contactInfo = userError.message;
          return;
        } else {
          // For other user creation errors, show generic error
          errors.value.contactInfo =
            "Failed to create user profile. Please try again.";
          return;
        }
      }
    }

    const selectedLocation = availableLocations.value.find(
      location => location.name === reportData.value.location
    );

    if (!selectedLocation) {
      console.error("Selected location not found");
      return;
    }

    const reportCreateData: ReportCreateData = {
      userId: user.id,
      locationId: selectedLocation.id,
      type: reportData.value.type!,
      items: reportData.value.items,
    };

    const newReport = await reportStore.createReport(reportCreateData);

    if (newReport) {
      console.log("Report submitted successfully:", newReport);
      // Navigate to the report details page
      router.push(`/reports/${newReport.id}`);
    }
  } catch (error) {
    console.error("Error submitting report:", error);
  }
};

watch(
  () => reportData.value.type,
  () => {
    if (errors.value.type) validateField("type");
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

watch(
  () => reportData.value.contactInfo,
  () => {
    if (errors.value.contactInfo) validateField("contactInfo");
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

.title-icon {
  font-size: 20px;
  color: var(--ion-color-primary);
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

.items-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.item-card {
  background: var(--ion-color-light-tint);
  border-radius: 12px;
  padding: 20px;
  border: 2px solid var(--ion-color-light-shade);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.item-header h4 {
  margin: 0;
  color: var(--ion-color-dark);
  font-size: 1.1em;
  font-weight: 600;
}

.item-inputs {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.add-item-button {
  --background: transparent;
  --color: var(--ion-color-primary);
  --border-color: var(--ion-color-primary);
  --border-width: 2px;
  --border-style: dashed;
  --border-radius: 12px;
  font-weight: 600;
  height: 48px;
  margin-top: 8px;
}

.add-item-button:hover {
  --background: var(--ion-color-primary-tint);
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
