<template>
	<template-page
		:headline="'Report Item'"
		:leftFooterButton="leftFooterButton"
		:rightFooterButton="rightFooterButton">
		<ion-list class="ion-padding">
			<ion-item>
				<ion-label position="stacked">Name</ion-label>
				<ion-input
					v-model="item.name"
					placeholder="Enter item name"></ion-input>
			</ion-item>
			<ion-item>
				<ion-label position="stacked">Description</ion-label>
				<ion-textarea
					v-model="item.description"
					placeholder="Enter description"></ion-textarea>
			</ion-item>
			<ion-item>
				<ion-label position="stacked">Location</ion-label>
				<ion-input
					v-model="item.location"
					placeholder="Enter location"></ion-input>
			</ion-item>
			<ion-item>
				<ion-label position="stacked">Status</ion-label>
				<ion-select v-model="item.status" placeholder="Select status">
					<ion-select-option value="Lost">Lost</ion-select-option>
					<ion-select-option value="Found">Found</ion-select-option>
				</ion-select>
			</ion-item>
		</ion-list>
	</template-page>
</template>

<script setup lang="ts">
import TemplatePage from '@/components/TemplatePage.vue';
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const leftFooterButton = {
	name: 'Cancel',
	color: 'danger',
};
const rightFooterButton = {
	name: 'Report Item',
	color: 'primary',
};

const item = ref({
	name: '',
	description: '',
	location: '',
	status: '',
});
</script>
