<template>
  <template-page
    :headline="'Add New Location'"
    :leftFooterButton="leftFooterButton"
    :rightFooterButton="rightFooterButton"
    @leftFooterButtonClicked="handleCancel"
    @rightFooterButtonClicked="handleSave"
  >
    <div class="form-container">
      <div class="progress-section">
        <ion-progress-bar
          :value="completionProgress"
          color="primary"
          class="progress-bar"
        ></ion-progress-bar>
        <p class="progress-text">
          {{ Math.round(completionProgress * 100) }}% completed
        </p>
      </div>

      <div class="form-header">
        <ion-icon :icon="locationOutline" class="header-icon"></ion-icon>
        <p>Create a new location for the Campus Lost & Found System</p>
      </div>

      <div class="form-content">
        <div class="input-group">
          <ion-input
            v-model="location.name"
            label="Location Name *"
            label-placement="stacked"
            placeholder="e.g. Main Library, North Cafeteria, Lecture Hall A"
            class="modern-input"
            :class="{
              'input-filled': location.name,
              'input-error': errors.name,
            }"
            autocomplete="off"
            autocorrect="off"
            autocapitalize="words"
            :spellcheck="false"
            @ionBlur="validateField('name')"
          ></ion-input>
          <div v-if="errors.name" class="error-message">
            <ion-icon :icon="alertCircleOutline"></ion-icon>
            {{ errors.name }}
          </div>
        </div>

        <div class="input-group">
          <ion-textarea
            v-model="location.description"
            label="Description"
            label-placement="stacked"
            placeholder="Detailed description of the location and its features..."
            class="modern-textarea"
            :class="{
              'textarea-filled': location.description,
            }"
            :auto-grow="true"
            :rows="3"
            autocomplete="off"
            autocorrect="off"
            :spellcheck="false"
          ></ion-textarea>
        </div>
      </div>
    </div>
  </template-page>
</template>

<script setup lang="ts">
import TemplatePage from "@/components/TemplatePage.vue";
import { IonInput, IonTextarea, IonIcon, IonProgressBar } from "@ionic/vue";
import {
  locationOutline,
  alertCircleOutline,
  checkmarkCircleOutline,
  closeCircleOutline,
} from "ionicons/icons";
import { ref, computed, watch } from "vue";
import { useRouter } from "vue-router";
import { useLocationStore } from "@/stores/locationStore";
import type { LocationCreateData } from "@/models/location";

const router = useRouter();
const locationStore = useLocationStore();

const location = ref<LocationCreateData>({
  name: "",
  description: "",
});

const errors = ref({
  name: "",
});

const isLoading = computed(() => locationStore.isLoading);

const leftFooterButton = computed(() => ({
  name: "Cancel",
  color: "medium",
  icon: closeCircleOutline,
}));

const rightFooterButton = computed(() => ({
  name: isLoading.value ? "Creating..." : "Add Location",
  color: isValid.value ? "primary" : "medium",
  icon: checkmarkCircleOutline,
  disabled: !isValid.value || isLoading.value,
}));

const completionProgress = computed(() => {
  const fields = ["name", "description"];
  const filledFields = fields.filter(field => {
    const value = location.value[field as keyof typeof location.value];
    return value ? value.trim() !== "" : false;
  });
  return filledFields.length / fields.length;
});

const isValid = computed(() => {
  return (
    location.value.name.trim() !== "" &&
    Object.values(errors.value).every(error => error === "")
  );
});

const validateField = (fieldName: keyof typeof errors.value) => {
  const fieldValue = location.value[fieldName as keyof typeof location.value];
  const value = fieldValue ? fieldValue.trim() : "";

  switch (fieldName) {
    case "name":
      if (!value) {
        errors.value.name = "Name is required";
      } else if (value.length < 2) {
        errors.value.name = "Name must be at least 2 characters long";
      } else {
        errors.value.name = "";
      }
      break;
  }
};

const validateAllFields = () => {
  validateField("name");
};

const handleCancel = () => {
  router.back();
};

const handleSave = async () => {
  if (!isValid.value) {
    validateAllFields();
    return;
  }

  try {
    const trimmedDescription = location.value.description?.trim();
    const newLocation = await locationStore.createLocation({
      name: location.value.name.trim(),
      description: trimmedDescription || undefined,
    });

    if (newLocation) {
      router.push(`/locations/${newLocation.id}`);
    } else {
      throw new Error("Failed to create location");
    }
  } catch (error) {
    console.error("Error creating location:", error);
  }
};

watch(
  () => location.value.name,
  () => {
    if (errors.value.name) errors.value.name = "";
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
  height: 6px;
  border-radius: 3px;
  margin-bottom: 8px;
  background: var(--ion-color-light);
}

.progress-text {
  font-size: 0.85em;
  color: var(--ion-color-medium);
  margin: 0;
  font-weight: 600;
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
  margin: 0;
  font-size: 1em;
  line-height: 1.4;
}

.form-content {
  animation: slideInUp 0.6s ease-out;
}

.input-group {
  margin-bottom: 24px;
}

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

.modern-input.input-filled,
.modern-textarea.textarea-filled {
  border-color: var(--ion-color-primary-tint);
  --background: rgba(var(--ion-color-primary-rgb), 0.05);
}

.modern-input:focus-within,
.modern-textarea:focus-within {
  border-color: var(--ion-color-primary);
  box-shadow: 0 0 0 3px rgba(var(--ion-color-primary-rgb), 0.1);
  transform: translateY(-2px);
}

.modern-textarea {
  min-height: 80px;
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

.form-footer-info {
  margin-top: 40px;
  padding: 20px;
  background: linear-gradient(
    135deg,
    var(--ion-color-light-tint),
    var(--ion-color-light)
  );
  border-radius: 12px;
  border-left: 4px solid var(--ion-color-primary);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  background: transparent;
  padding: 0;
}

.info-icon {
  font-size: 20px;
  margin-top: 2px;
  flex-shrink: 0;
}

.info-text {
  flex: 1;
}

.info-text p {
  margin: 0 0 8px 0;
  color: var(--ion-color-medium-shade);
  font-size: 0.9em;
  line-height: 1.4;
}

.info-text p:last-child {
  margin-bottom: 0;
}

.required-note {
  font-size: 0.8em !important;
  color: var(--ion-color-medium) !important;
  font-style: italic;
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

  .input-row {
    flex-direction: column;
    gap: 0;
  }

  .half-width {
    flex: none;
  }

  .form-header {
    margin-bottom: 30px;
  }

  .header-icon {
    font-size: 48px;
  }

  .form-header h2 {
    font-size: 1.5em;
  }
}

@media (max-width: 480px) {
  .progress-section {
    margin-bottom: 20px;
  }

  .form-header {
    margin-bottom: 20px;
  }

  .input-group {
    margin-bottom: 20px;
  }

  .form-footer-info {
    margin-top: 30px;
    padding: 16px;
  }
}
</style>
