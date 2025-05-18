<template>
	<template-page
		:headline="'Edit Location'"
		:leftFooterButton="leftFooterButton"
		:rightFooterButton="rightFooterButton">
		<ion-list class="ion-padding">
			<ion-item>
				<ion-label position="stacked">Name</ion-label>
				<ion-input
					v-model="location.name"
					placeholder="Enter location name"></ion-input>
			</ion-item>
			<ion-item>
				<ion-label position="stacked">Description</ion-label>
				<ion-textarea
					v-model="location.description"
					placeholder="Enter description"></ion-textarea>
			</ion-item>
			<ion-item>
				<ion-label position="stacked">Building</ion-label>
				<ion-input
					v-model="location.building"
					placeholder="Enter building"></ion-input>
			</ion-item>
			<ion-item>
				<ion-label position="stacked">Floor</ion-label>
				<ion-input
					v-model="location.floor"
					placeholder="Enter floor"></ion-input>
			</ion-item>
			<ion-item>
				<ion-label position="stacked">Room</ion-label>
				<ion-input v-model="location.room" placeholder="Enter room"></ion-input>
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
	name: 'Save',
	color: 'primary',
};

const location = ref({
	name: '',
	description: '',
	building: '',
	floor: '',
	room: '',
});
</script>
