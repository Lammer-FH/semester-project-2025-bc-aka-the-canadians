<template>
  <template-page
    :headline="'Edit Location'"
    :leftFooterButton="leftFooterButton"
    :rightFooterButton="rightFooterButton"
    @leftFooterButtonClicked="handleCancel"
    @rightFooterButtonClicked="handleSave"
  >
    <div class="form-container">
      <div v-if="isLoading" class="loading-container">
        <ion-spinner name="crescent" color="primary"></ion-spinner>
        <p>Loading location...</p>
      </div>

      <div v-else-if="error && !location.name" class="empty-state">
        <ion-icon :icon="alertCircleOutline" class="empty-icon"></ion-icon>
        <h2>Error Loading</h2>
        <p>{{ error }}</p>
        <ion-button @click="loadLocation">
          <ion-icon :icon="refreshOutline" slot="start"></ion-icon>
          Try Again
        </ion-button>
      </div>

      <div v-else class="form-content">
        <div class="form-header">
          <ion-icon :icon="createOutline" class="header-icon"></ion-icon>
          <p>Update the details for this location</p>
          <div v-if="location.createdAt" class="last-modified">
            <ion-icon :icon="timeOutline" class="time-icon"></ion-icon>
            Created: {{ formatDate(location.createdAt) }}
          </div>
        </div>

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

        <div class="action-buttons">
          <ion-button
            fill="outline"
            color="danger"
            expand="block"
            class="delete-button"
            @click="deleteLocation"
          >
            <ion-icon :icon="trashOutline" slot="start"></ion-icon>
            Delete Location
          </ion-button>
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
  IonButton,
  IonSpinner,
} from "@ionic/vue";
import {
  createOutline,
  alertCircleOutline,
  checkmarkCircleOutline,
  closeCircleOutline,
  trashOutline,
  timeOutline,
  refreshOutline,
} from "ionicons/icons";
import { ref, computed, watch, onMounted, nextTick } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useLocationStore } from "@/stores/locationStore";
import type { Location } from "@/models/location";

const router = useRouter();
const route = useRoute();
const locationStore = useLocationStore();

const location = ref<Location>({
  id: 0,
  name: "",
  description: "",
  createdAt: "",
});

const errors = ref({
  name: "",
  description: "",
});

const isLoading = ref(false);
const error = ref<string | null>(null);
const isSaving = ref(false);
const originalLocation = ref<Location | null>(null);

const leftFooterButton = computed(() => ({
  name: "Cancel",
  color: "medium",
  icon: closeCircleOutline,
}));

const rightFooterButton = computed(() => ({
  name: isSaving.value ? "Saving..." : "Save Changes",
  color: isValid.value ? "primary" : "medium",
  icon: checkmarkCircleOutline,
  disabled: !isValid.value || isSaving.value || isLoading.value,
}));

const isValid = computed(() => {
  try {
    const nameValid =
      location.value?.name?.trim() !== "" &&
      Object.values(errors.value).every(error => error === "");
    if (!nameValid) return false;
    if (!originalLocation.value) return false;
    // Only enable if something changed
    return (
      location.value.name.trim() !== originalLocation.value.name.trim() ||
      (location.value.description?.trim() || "") !==
        (originalLocation.value.description?.trim() || "")
    );
  } catch (error) {
    console.error("Error in isValid computed:", error);
    return false;
  }
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
    const value = String(
      location.value?.[fieldName as keyof typeof location.value] || ""
    ).trim();

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
  } catch (error) {
    console.error("Error validating field:", fieldName, error);
  }
};

const validateAllFields = () => {
  try {
    validateField("name");
  } catch (error) {
    console.error("Error validating all fields:", error);
  }
};

const loadLocation = async () => {
  try {
    isLoading.value = true;
    error.value = null;

    const locationId = Number(route.params.id);
    if (!locationId || isNaN(locationId)) {
      throw new Error("Invalid location ID");
    }

    await locationStore.fetchLocationById(locationId);
    const loadedLocation = locationStore.getCurrentLocation;

    if (loadedLocation) {
      await nextTick();
      location.value = { ...loadedLocation };
      originalLocation.value = { ...loadedLocation };
    } else {
      throw new Error("Location not found");
    }
  } catch (err) {
    console.error("Error loading location:", err);
    error.value = err instanceof Error ? err.message : "Unknown error";
    setTimeout(() => {
      router.back();
    }, 2000);
  } finally {
    isLoading.value = false;
  }
};

const handleCancel = () => {
  try {
    router.back();
  } catch (error) {
    console.error("Error navigating back:", error);
    router.push("/locations/overview");
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
      name: location.value.name.trim(),
      description: location.value.description?.trim() || "",
    };

    await locationStore.updateLocation(location.value.id, updateData);

    router.push(`/locations/${location.value.id}`);
  } catch (error) {
    console.error("Error saving location:", error);
  } finally {
    isSaving.value = false;
  }
};

const deleteLocation = async () => {
  try {
    isSaving.value = true;
    await locationStore.deleteLocation(location.value.id);
    router.push("/locations/overview");
  } catch (error) {
    console.error("Error deleting location:", error);
  } finally {
    isSaving.value = false;
  }
};

watch(
  () => location.value?.name,
  newName => {
    try {
      if (newName && errors.value.name) {
        errors.value.name = "";
      }
    } catch (error) {
      console.error("Error in name watcher:", error);
    }
  }
);

onMounted(async () => {
  try {
    await loadLocation();
  } catch (error) {
    console.error("Error in onMounted:", error);
  }
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

.form-header {
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

.form-content {
  animation: slideInUp 0.6s ease-out;
}

.input-group {
  margin-bottom: 24px;
}

.input-row {
  display: flex;
  gap: 16px;
}

.half-width {
  flex: 1;
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
  position: relative;
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
  --padding-start: 16px;
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

.action-buttons {
  margin: 40px 0;
  padding: 20px;
  border-top: 1px solid var(--ion-color-light-shade);
  border-bottom: 1px solid var(--ion-color-light-shade);
}

.delete-button {
  width: 100%;
  --border-radius: 12px;
  height: 48px;
  font-weight: 600;
}

.form-footer-info {
  margin-top: 20px;
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
    font-size: 40px;
  }

  .last-modified {
    padding: 6px 12px;
    font-size: 0.8em;
  }
}

@media (max-width: 480px) {
  .form-header {
    margin-bottom: 20px;
  }

  .input-group {
    margin-bottom: 20px;
  }

  .action-buttons {
    margin: 30px 0;
    padding: 16px;
  }
}
</style>
