<template>
  <ion-app>
    <router-view></router-view>
  </ion-app>
</template>

<script setup>
import { IonApp } from "@ionic/vue";
import { onMounted } from "vue";
import { useUserStore } from "@/stores/userStore";

const userStore = useUserStore();

onMounted(async () => {
  // Initialize user from localStorage on app startup
  await userStore.initializeUser();
});
</script>

<style>
:root {
  --ion-color-primary: #3880ff;
  --ion-color-secondary: #3dc2ff;
  --ion-color-tertiary: #5260ff;
  --ion-color-success: #2dd36f;
  --ion-color-warning: #ffc409;
  --ion-color-danger: #eb445a;
  --ion-color-medium: #92949c;
  --ion-color-light: #f4f5f8;
}
</style>
