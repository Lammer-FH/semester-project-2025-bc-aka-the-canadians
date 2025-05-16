<template>
	<ion-segment :value="modelValue" @ionChange="onTabChange">
		<ion-segment-button value="items">
			<ion-label>Items</ion-label>
		</ion-segment-button>
		<ion-segment-button value="locations">
			<ion-label>Locations</ion-label>
		</ion-segment-button>
	</ion-segment>
</template>

<script setup lang="ts">
import { IonSegment, IonSegmentButton, IonLabel } from '@ionic/vue';

const props = defineProps<{ modelValue: string }>();
const emit = defineEmits<(e: 'update:modelValue', value: string) => void>();

function onTabChange(event: CustomEvent) {
	emit('update:modelValue', event.detail.value);
}
</script>

<style scoped>
ion-segment {
	width: 100%;
}
</style>
