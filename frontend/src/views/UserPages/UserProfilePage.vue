<template>
	<template-page
		:showProfileButton="false"
		:leftFooterButton="leftFooterButton"
		:rightFooterButton="rightFooterButton"
		:headline="'User Profile'">
		<ion-list class="ion-padding">
			<ion-item>
				<ion-label position="stacked">Name</ion-label>
				<div class="input-row">
					<ion-input readonly :value="user.name"></ion-input>
					<ion-button color="success" size="small">Edit</ion-button>
				</div>
			</ion-item>
			<ion-item>
				<ion-label position="stacked">Phone</ion-label>
				<div class="input-row">
					<ion-input readonly :value="user.phone"></ion-input>
					<ion-button color="success" size="small">Edit</ion-button>
				</div>
			</ion-item>
			<ion-item>
				<ion-label position="stacked">Email</ion-label>
				<div class="input-row">
					<ion-input readonly :value="user.email"></ion-input>
					<ion-button color="success" size="small">Edit</ion-button>
				</div>
			</ion-item>
		</ion-list>
	</template-page>
</template>

<script setup lang="ts">
import TemplatePage from '@/components/TemplatePage.vue';
import { IonList, IonItem, IonLabel, IonInput, IonButton } from '@ionic/vue';
import { useRouter } from 'vue-router';

const router = useRouter();

//replace with actual data later on
const user = {
	name: 'John Doe',
	phone: '+43 123 456789',
	email: 'john.doe@example.com',
};

const leftFooterButton = {
	name: 'Cancel',
	color: 'danger',
};
const rightFooterButton = {
	name: 'Save',
	color: 'primary',
};
</script>

<style scoped>
.input-row {
	display: flex;
	align-items: center;
	width: 100%;
}
.input-row ion-input {
	flex: 1;
}
</style>
