<template>
	<template-page
		:headline="'Item Details'"
		:leftFooterButton="leftFooterButton"
		:rightFooterButton="rightFooterButton">
		<ion-list class="ion-padding">
			<ion-item>
				<ion-label position="stacked">Name</ion-label>
				<ion-input readonly :value="item.name"></ion-input>
			</ion-item>
			<ion-item>
				<ion-label position="stacked">Description</ion-label>
				<ion-textarea readonly :value="item.description"></ion-textarea>
			</ion-item>
			<ion-item>
				<ion-label position="stacked">Location</ion-label>
				<ion-input readonly :value="item.location"></ion-input>
			</ion-item>
			<ion-item>
				<ion-label position="stacked">Status</ion-label>
				<ion-input readonly :value="item.status"></ion-input>
			</ion-item>
			<ion-item>
				<ion-label position="stacked">Created</ion-label>
				<ion-input readonly :value="formatDate(item.createdAt)"></ion-input>
			</ion-item>
			<ion-item>
				<ion-label position="stacked">Updated</ion-label>
				<ion-input readonly :value="formatDate(item.updatedAt)"></ion-input>
			</ion-item>
		</ion-list>
		<ion-button color="primary" expand="block">Claim Item</ion-button>
	</template-page>
</template>

<script setup lang="ts">
import TemplatePage from '@/components/TemplatePage.vue';
import {
	IonList,
	IonItem,
	IonLabel,
	IonInput,
	IonTextarea,
	IonButton,
} from '@ionic/vue';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import type { Item } from '@/models/item';

const router = useRouter();

const leftFooterButton = {
	name: 'Cancel',
	color: 'danger',
};
const rightFooterButton = {
	name: 'Edit Item',
	color: 'success',
};

// replace with actual data later on
const item = ref<Item>({
	id: 1,
	name: 'Backpack',
	description: 'Black backpack with laptop',
	location: 'Library',
	status: 'Lost',
	createdAt: '2024-01-01T10:00:00Z',
	updatedAt: '2024-01-02T12:00:00Z',
});

function formatDate(dateStr: string) {
	return new Date(dateStr).toLocaleString();
}
</script>
