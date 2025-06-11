<template>
  <template-page
    :showProfileButton="false"
    :leftFooterButton="leftFooterButton"
    :rightFooterButton="rightFooterButton"
    :headline="'My Profile'"
    @leftFooterButtonClicked="handleCancel"
    @rightFooterButtonClicked="handleSave"
  >
    <div class="profile-container">
      <div v-if="isLoading" class="loading-container">
        <ion-spinner name="crescent" color="primary"></ion-spinner>
        <p>Loading Profile...</p>
      </div>

      <div v-else-if="error && !user" class="empty-state">
        <ion-icon :icon="alertCircleOutline" class="empty-icon"></ion-icon>
        <h2>Loading Error</h2>
        <p>{{ error }}</p>
        <ion-button @click="loadUserProfile">
          <ion-icon :icon="refreshOutline" slot="start"></ion-icon>
          Try Again
        </ion-button>
      </div>

      <div v-else-if="user" class="content-wrapper">
        <div class="profile-form">
          <h3>Personal Information</h3>

          <div class="form-group">
            <ion-item
              class="modern-item"
              :class="{
                'item-error': errors.name,
                'item-filled': editData.name,
              }"
            >
              <ion-label position="stacked" class="custom-label">
                <ion-icon :icon="personOutline" class="label-icon"></ion-icon>
                Name *
              </ion-label>
              <ion-input
                v-model="editData.name"
                placeholder="Enter your name"
                @ionBlur="validateField('name')"
                :class="{ 'input-error': errors.name }"
                autofocus
              ></ion-input>
            </ion-item>
            <div v-if="errors.name" class="error-message">
              <ion-icon :icon="alertCircleOutline"></ion-icon>
              {{ errors.name }}
            </div>
          </div>

          <div class="form-group">
            <ion-item
              class="modern-item"
              :class="{
                'item-error': errors.email,
                'item-filled': editData.email,
              }"
            >
              <ion-label position="stacked" class="custom-label">
                <ion-icon :icon="mailOutline" class="label-icon"></ion-icon>
                Email Address *
              </ion-label>
              <ion-input
                v-model="editData.email"
                type="email"
                placeholder="your.email@example.com"
                @ionBlur="validateField('email')"
                :class="{ 'input-error': errors.email }"
              ></ion-input>
            </ion-item>
            <div v-if="errors.email" class="error-message">
              <ion-icon :icon="alertCircleOutline"></ion-icon>
              {{ errors.email }}
            </div>
          </div>
        </div>

        <div class="danger-zone">
          <ion-button
            fill="outline"
            color="danger"
            expand="block"
            class="delete-button"
            @click="deleteAccount"
          >
            <ion-icon :icon="trashOutline" slot="start"></ion-icon>
            Delete Account
          </ion-button>
        </div>
      </div>
    </div>
  </template-page>
</template>

<script setup lang="ts">
import TemplatePage from "@/components/TemplatePage.vue";
import {
  IonItem,
  IonLabel,
  IonInput,
  IonButton,
  IonIcon,
  IonSpinner,
} from "@ionic/vue";
import {
  personOutline,
  mailOutline,
  alertCircleOutline,
  trashOutline,
  closeOutline,
  saveOutline,
  refreshOutline,
} from "ionicons/icons";
import { ref, computed, reactive, watch, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";
import type { UserUpdateData } from "@/models/user";

const router = useRouter();
const userStore = useUserStore();

const user = computed(() => userStore.getCurrentUser);
const isLoading = computed(() => userStore.isLoading);
const error = computed(() => userStore.getError);

// Mock user ID for now - in a real app, this would come from authentication
const CURRENT_USER_ID = 1;

const userStats = ref({
  itemsReported: 0,
  itemsClaimed: 0,
  helpfulReports: 0,
});

const editData = reactive({
  name: "",
  email: "",
});

const errors = ref({
  name: "",
  email: "",
});

const hasChanges = ref(false);

const leftFooterButton = computed(() => ({
  name: "Back",
  color: "medium",
  icon: closeOutline,
}));

const rightFooterButton = computed(() => ({
  name: hasChanges.value ? "Save Changes" : "Saved",
  color: hasChanges.value ? "primary" : "medium",
  icon: saveOutline,
  disabled: !hasChanges.value,
}));

onMounted(async () => {
  await loadUserProfile();
  if (user.value) {
    editData.name = user.value.name;
    editData.email = user.value.email;
  }
});

const loadUserProfile = async () => {
  try {
    await userStore.fetchUserById(CURRENT_USER_ID);
    loadUserStats();
  } catch (error) {
    console.error("Error loading user profile:", error);
  }
};

const loadUserStats = async () => {
  userStats.value = {
    itemsReported: 12,
    itemsClaimed: 8,
    helpfulReports: 15,
  };
};

const validateField = (fieldName: keyof typeof errors.value) => {
  const value = editData[fieldName as keyof typeof editData]?.trim() || "";

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
    case "email":
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!value) {
        errors.value.email = "Email address is required";
      } else if (!emailRegex.test(value)) {
        errors.value.email = "Please enter a valid email address";
      } else {
        errors.value.email = "";
      }
      break;
  }
};

const handleCancel = () => {
  if (hasChanges.value && user.value) {
    editData.name = user.value.name;
    editData.email = user.value.email;
    hasChanges.value = false;
  }
  router.back();
};

const handleSave = async () => {
  alert("Need to implement save/update functionality");
};

const deleteAccount = () => {
  alert("Need to implement delete account functionality");
};

watch(
  [() => editData.name, () => editData.email],
  () => {
    if (user.value) {
      const hasNameChanged = editData.name !== user.value.name;
      const hasEmailChanged = editData.email !== user.value.email;
      hasChanges.value = hasNameChanged || hasEmailChanged;
    }
  },
  { deep: true }
);
</script>

<style scoped>
.profile-container {
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

.content-wrapper {
  padding: 20px;
  animation: fadeInUp 0.6s ease-out;
}

.profile-header {
  background: linear-gradient(
    135deg,
    var(--ion-color-primary),
    var(--ion-color-primary-shade)
  );
  color: white;
  padding: 30px 24px;
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 8px 24px rgba(var(--ion-color-primary-rgb), 0.3);
}

.user-info {
  text-align: center;
}

.user-info h1 {
  color: white;
  margin: 0 0 8px 0;
  font-size: 1.5em;
  font-weight: 600;
}

.user-info p {
  color: rgba(255, 255, 255, 0.9);
  margin: 0 0 12px 0;
  font-size: 0.95em;
}

.user-id,
.created-at {
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.85em;
  margin: 4px 0;
}

.chip-icon {
  font-size: 14px;
  margin-right: 4px;
}

.profile-form,
.account-actions,
.statistics-section,
.danger-zone {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.profile-form h3,
.account-actions h3,
.statistics-section h3,
.danger-zone h3 {
  color: var(--ion-color-dark);
  margin: 0 0 20px 0;
  font-size: 1.1em;
  font-weight: 600;
}

.form-group {
  margin-bottom: 20px;
}

.modern-item {
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

.modern-item.item-filled {
  border-color: var(--ion-color-primary-tint);
  --background: rgba(var(--ion-color-primary-rgb), 0.05);
}

.modern-item.item-editing {
  border-color: var(--ion-color-primary);
  box-shadow: 0 0 0 3px rgba(var(--ion-color-primary-rgb), 0.1);
}

.modern-item.item-error {
  border-color: var(--ion-color-danger);
  --background: rgba(var(--ion-color-danger-rgb), 0.05);
}

.custom-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: var(--ion-color-dark);
  margin-bottom: 4px;
}

.label-icon {
  font-size: 16px;
  color: var(--ion-color-primary);
}

.display-value {
  color: var(--ion-color-dark);
  font-size: 16px;
  padding: 8px 0;
}

.input-error {
  color: var(--ion-color-danger);
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

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: var(--ion-color-light-tint);
  border-radius: 8px;
  border-left: 4px solid var(--ion-color-primary);
}

.stat-icon {
  font-size: 24px;
  color: var(--ion-color-primary);
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-number {
  font-size: 1.5em;
  font-weight: 700;
  color: var(--ion-color-dark);
  line-height: 1;
}

.stat-label {
  font-size: 0.8em;
  color: var(--ion-color-medium);
  margin-top: 2px;
}

.delete-button {
  --border-radius: 12px;
  height: 48px;
  font-weight: 600;
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
  .content-wrapper {
    padding: 12px;
  }

  .user-info {
    text-align: center;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .stat-card {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .profile-header {
    padding: 20px 16px;
  }

  .user-info h1 {
    font-size: 1.3em;
  }
}
</style>
