<template>
  <ion-segment :value="modelValue" @ionChange="onTabChange">
    <ion-segment-button value="items">
      <ion-label>Items</ion-label>
    </ion-segment-button>
    <ion-segment-button value="reports">
      <ion-label>Reports</ion-label>
    </ion-segment-button>
    <ion-segment-button value="locations">
      <ion-label>Locations</ion-label>
    </ion-segment-button>
  </ion-segment>
</template>

<script setup lang="ts">
import { IonSegment, IonSegmentButton, IonLabel } from "@ionic/vue";
import { useRouter } from "vue-router";

defineProps<{ modelValue: string }>();
const emit = defineEmits<{ "update:modelValue": [value: string] }>();

const router = useRouter();

function onTabChange(event: CustomEvent) {
  const value = event.detail.value;
  emit("update:modelValue", value);

  switch (value) {
    case "items":
      router.push("/items/overview");
      break;
    case "reports":
      router.push("/reports/overview");
      break;
    case "locations":
      router.push("/locations/overview");
      break;
  }
}
</script>

<style scoped>
ion-segment {
  width: 100%;
}
</style>
