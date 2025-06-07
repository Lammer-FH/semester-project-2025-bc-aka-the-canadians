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
        <div class="profile-header">
          <div class="avatar-section">
            <div class="avatar-container">
              <img
                v-if="user.avatar"
                :src="user.avatar"
                :alt="getFullName(user)"
                class="avatar-image"
              />
              <div v-else class="avatar-placeholder">
                <ion-icon :icon="personOutline" class="avatar-icon"></ion-icon>
              </div>
              <ion-button
                fill="clear"
                size="small"
                class="avatar-edit-button"
                @click="handleAvatarEdit"
              >
                <ion-icon :icon="cameraOutline" slot="icon-only"></ion-icon>
              </ion-button>
            </div>
            <div class="user-info">
              <h1>{{ getFullName(user) }}</h1>
              <p>{{ user.email }}</p>
              <ion-chip color="primary" outline>
                <ion-icon :icon="timeOutline" class="chip-icon"></ion-icon>
                Member since {{ formatDate(user.createdAt) }}
              </ion-chip>
            </div>
          </div>
        </div>

        <div class="profile-form">
          <h3>Personal Information</h3>

          <div class="form-group">
            <ion-item
              class="modern-item"
              :class="{
                'item-editing': editingField === 'firstName',
                'item-error': errors.firstName,
                'item-filled': user.firstName,
              }"
            >
              <ion-label position="stacked" class="custom-label">
                <ion-icon :icon="personOutline" class="label-icon"></ion-icon>
                First Name *
              </ion-label>
              <ion-input
                v-if="editingField === 'firstName'"
                v-model="editData.firstName"
                placeholder="Enter your first name"
                @ionBlur="validateField('firstName')"
                :class="{ 'input-error': errors.firstName }"
                autofocus
              ></ion-input>
              <div v-else class="display-value">{{ user.firstName }}</div>
              <ion-button
                fill="clear"
                size="small"
                slot="end"
                :color="editingField === 'firstName' ? 'success' : 'primary'"
                @click="toggleEdit('firstName')"
              >
                <ion-icon
                  :icon="
                    editingField === 'firstName'
                      ? checkmarkOutline
                      : createOutline
                  "
                  slot="icon-only"
                ></ion-icon>
              </ion-button>
            </ion-item>
            <div v-if="errors.firstName" class="error-message">
              <ion-icon :icon="alertCircleOutline"></ion-icon>
              {{ errors.firstName }}
            </div>
          </div>

          <div class="form-group">
            <ion-item
              class="modern-item"
              :class="{
                'item-editing': editingField === 'lastName',
                'item-error': errors.lastName,
                'item-filled': user.lastName,
              }"
            >
              <ion-label position="stacked" class="custom-label">
                <ion-icon :icon="personOutline" class="label-icon"></ion-icon>
                Last Name *
              </ion-label>
              <ion-input
                v-if="editingField === 'lastName'"
                v-model="editData.lastName"
                placeholder="Enter your last name"
                @ionBlur="validateField('lastName')"
                :class="{ 'input-error': errors.lastName }"
                autofocus
              ></ion-input>
              <div v-else class="display-value">{{ user.lastName }}</div>
              <ion-button
                fill="clear"
                size="small"
                slot="end"
                :color="editingField === 'lastName' ? 'success' : 'primary'"
                @click="toggleEdit('lastName')"
              >
                <ion-icon
                  :icon="
                    editingField === 'lastName'
                      ? checkmarkOutline
                      : createOutline
                  "
                  slot="icon-only"
                ></ion-icon>
              </ion-button>
            </ion-item>
            <div v-if="errors.lastName" class="error-message">
              <ion-icon :icon="alertCircleOutline"></ion-icon>
              {{ errors.lastName }}
            </div>
          </div>

          <div class="form-group">
            <ion-item
              class="modern-item"
              :class="{
                'item-editing': editingField === 'email',
                'item-error': errors.email,
                'item-filled': user.email,
              }"
            >
              <ion-label position="stacked" class="custom-label">
                <ion-icon :icon="mailOutline" class="label-icon"></ion-icon>
                Email Address *
              </ion-label>
              <ion-input
                v-if="editingField === 'email'"
                v-model="editData.email"
                type="email"
                placeholder="your.email@example.com"
                @ionBlur="validateField('email')"
                :class="{ 'input-error': errors.email }"
                autofocus
              ></ion-input>
              <div v-else class="display-value">{{ user.email }}</div>
              <ion-button
                fill="clear"
                size="small"
                slot="end"
                :color="editingField === 'email' ? 'success' : 'primary'"
                @click="toggleEdit('email')"
              >
                <ion-icon
                  :icon="
                    editingField === 'email' ? checkmarkOutline : createOutline
                  "
                  slot="icon-only"
                ></ion-icon>
              </ion-button>
            </ion-item>
            <div v-if="errors.email" class="error-message">
              <ion-icon :icon="alertCircleOutline"></ion-icon>
              {{ errors.email }}
            </div>
          </div>

          <div class="form-group">
            <ion-item class="modern-item item-filled">
              <ion-label position="stacked" class="custom-label">
                <ion-icon :icon="personOutline" class="label-icon"></ion-icon>
                Username
              </ion-label>
              <div class="display-value">{{ user.username }}</div>
              <ion-note slot="end" color="medium">Not changeable</ion-note>
            </ion-item>
          </div>
        </div>

        <div class="account-actions">
          <h3>Account Settings</h3>

          <ion-list class="action-list">
            <ion-item button @click="changePassword" class="action-item">
              <ion-icon
                :icon="lockClosedOutline"
                slot="start"
                color="primary"
              ></ion-icon>
              <ion-label>
                <h4>Change Password</h4>
                <p>Update your password for better security</p>
              </ion-label>
              <ion-icon
                :icon="chevronForwardOutline"
                slot="end"
                color="medium"
              ></ion-icon>
            </ion-item>

            <ion-item button @click="notificationSettings" class="action-item">
              <ion-icon
                :icon="notificationsOutline"
                slot="start"
                color="primary"
              ></ion-icon>
              <ion-label>
                <h4>Notifications</h4>
                <p>Manage email and push notifications</p>
              </ion-label>
              <ion-icon
                :icon="chevronForwardOutline"
                slot="end"
                color="medium"
              ></ion-icon>
            </ion-item>

            <ion-item button @click="privacySettings" class="action-item">
              <ion-icon
                :icon="shieldCheckmarkOutline"
                slot="start"
                color="primary"
              ></ion-icon>
              <ion-label>
                <h4>Privacy</h4>
                <p>Manage your privacy settings</p>
              </ion-label>
              <ion-icon
                :icon="chevronForwardOutline"
                slot="end"
                color="medium"
              ></ion-icon>
            </ion-item>
          </ion-list>
        </div>

        <div class="statistics-section">
          <h3>Your Activity</h3>
          <div class="stats-grid">
            <div class="stat-card">
              <ion-icon :icon="bagOutline" class="stat-icon"></ion-icon>
              <div class="stat-content">
                <div class="stat-number">{{ userStats.itemsReported }}</div>
                <div class="stat-label">Items Reported</div>
              </div>
            </div>
            <div class="stat-card">
              <ion-icon
                :icon="checkmarkCircleOutline"
                class="stat-icon"
              ></ion-icon>
              <div class="stat-content">
                <div class="stat-number">{{ userStats.itemsClaimed }}</div>
                <div class="stat-label">Items Collected</div>
              </div>
            </div>
            <div class="stat-card">
              <ion-icon :icon="trophyOutline" class="stat-icon"></ion-icon>
              <div class="stat-content">
                <div class="stat-number">{{ userStats.helpfulReports }}</div>
                <div class="stat-label">Helpful Reports</div>
              </div>
            </div>
          </div>
        </div>

        <div class="danger-zone">
          <h3>Danger Zone</h3>
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

    <ion-alert
      :is-open="showDeleteAlert"
      header="Delete Account"
      message="Are you sure you want to delete your account? This action cannot be undone and all your data will be permanently deleted."
      :buttons="deleteAlertButtons"
      @didDismiss="showDeleteAlert = false"
    ></ion-alert>
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
  IonChip,
  IonSpinner,
  IonAlert,
  IonList,
  IonNote,
} from "@ionic/vue";
import {
  personOutline,
  cameraOutline,
  timeOutline,
  createOutline,
  checkmarkOutline,
  mailOutline,
  alertCircleOutline,
  lockClosedOutline,
  notificationsOutline,
  shieldCheckmarkOutline,
  chevronForwardOutline,
  bagOutline,
  checkmarkCircleOutline,
  trophyOutline,
  trashOutline,
  closeOutline,
  saveOutline,
  refreshOutline,
} from "ionicons/icons";
import { ref, computed, reactive, watch, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";
import type { User, UserUpdateData } from "@/models/user";

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
  firstName: "",
  lastName: "",
  email: "",
});

const errors = ref({
  firstName: "",
  lastName: "",
  email: "",
});

const editingField = ref<string | null>(null);
const hasChanges = ref(false);
const showDeleteAlert = ref(false);

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

const deleteAlertButtons = [
  {
    text: "Cancel",
    role: "cancel",
    cssClass: "alert-button-cancel",
  },
  {
    text: "Delete Account",
    role: "destructive",
    cssClass: "alert-button-confirm",
    handler: () => confirmDeleteAccount(),
  },
];

onMounted(async () => {
  await loadUserProfile();
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

const getFullName = (user: User): string => {
  return `${user.firstName} ${user.lastName}`.trim();
};

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString("en-US", {
    month: "long",
    year: "numeric",
  });
};

const validateField = (fieldName: keyof typeof errors.value) => {
  const value = editData[fieldName as keyof typeof editData]?.trim() || "";

  switch (fieldName) {
    case "firstName":
      if (!value) {
        errors.value.firstName = "First name is required";
      } else if (value.length < 2) {
        errors.value.firstName =
          "First name must be at least 2 characters long";
      } else {
        errors.value.firstName = "";
      }
      break;
    case "lastName":
      if (!value) {
        errors.value.lastName = "Last name is required";
      } else if (value.length < 2) {
        errors.value.lastName = "Last name must be at least 2 characters long";
      } else {
        errors.value.lastName = "";
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

const toggleEdit = async (fieldName: string) => {
  if (editingField.value === fieldName) {
    validateField(fieldName as keyof typeof errors.value);

    if (!errors.value[fieldName as keyof typeof errors.value] && user.value) {
      user.value[fieldName as keyof User] =
        editData[fieldName as keyof typeof editData];
      editingField.value = null;
      hasChanges.value = true;
    }
  } else {
    if (user.value) {
      editData[fieldName as keyof typeof editData] = user.value[
        fieldName as keyof User
      ] as string;
      editingField.value = fieldName;
    }
  }
};

const handleCancel = () => {
  if (hasChanges.value) {
    hasChanges.value = false;
    editingField.value = null;
  }
  router.back();
};

const handleSave = async () => {
  if (!hasChanges.value || !user.value) return;

  try {
    const updateData: UserUpdateData = {
      firstName: user.value.firstName,
      lastName: user.value.lastName,
      email: user.value.email,
    };

    await userStore.updateUser(user.value.id, updateData);
    hasChanges.value = false;
    editingField.value = null;

    console.log("Profile updated successfully");
  } catch (error) {
    console.error("Error updating profile:", error);
  }
};

const handleAvatarEdit = () => {
  console.log("Avatar edit clicked");
};

const changePassword = () => {
  console.log("Change password clicked");
};

const notificationSettings = () => {
  console.log("Notification settings clicked");
};

const privacySettings = () => {
  console.log("Privacy settings clicked");
};

const deleteAccount = () => {
  showDeleteAlert.value = true;
};

const confirmDeleteAccount = async () => {
  try {
    console.log("Delete account confirmed");
    router.push("/");
  } catch (error) {
    console.error("Error deleting account:", error);
  }
};

watch(
  [() => editData.firstName, () => editData.lastName, () => editData.email],
  () => {
    if (user.value) {
      const hasFirstNameChanged = editData.firstName !== user.value.firstName;
      const hasLastNameChanged = editData.lastName !== user.value.lastName;
      const hasEmailChanged = editData.email !== user.value.email;

      hasChanges.value =
        hasFirstNameChanged || hasLastNameChanged || hasEmailChanged;
    }
  },
  { deep: true },
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

.avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-container {
  position: relative;
  flex-shrink: 0;
}

.avatar-image,
.avatar-placeholder {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-placeholder {
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-icon {
  font-size: 40px;
  color: rgba(255, 255, 255, 0.8);
}

.avatar-edit-button {
  position: absolute;
  bottom: -2px;
  right: -2px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--ion-color-primary-shade);
  border: 2px solid white;
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

.action-list {
  background: transparent;
}

.action-item {
  --background: var(--ion-color-light-tint);
  --border-radius: 8px;
  margin-bottom: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.action-item:hover {
  transform: translateX(4px);
}

.action-item h4 {
  margin: 0 0 4px 0;
  color: var(--ion-color-dark);
  font-weight: 600;
}

.action-item p {
  margin: 0;
  color: var(--ion-color-medium);
  font-size: 0.9em;
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

  .avatar-section {
    flex-direction: column;
    text-align: center;
    gap: 12px;
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

  .avatar-image,
  .avatar-placeholder {
    width: 60px;
    height: 60px;
  }

  .avatar-icon {
    font-size: 30px;
  }

  .user-info h1 {
    font-size: 1.3em;
  }
}
</style>
