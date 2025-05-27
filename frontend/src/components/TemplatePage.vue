<template>
	<ion-page>
		<ion-header>
			<ion-toolbar>
				<ion-title @click="navigateTo('/')">Lost & Found</ion-title>
				<ion-buttons slot="end" v-if="showProfileButton">
					<ion-button @click="navigateTo('/user')">
						<ion-icon :icon="personCircleOutline" size="large"></ion-icon>
					</ion-button>
				</ion-buttons>
			</ion-toolbar>
			<div v-if="headline" class="template-headline">
				{{ headline }}
			</div>
			<slot name="header"></slot>
		</ion-header>

		<ion-content>
			<slot></slot>
		</ion-content>

		<ion-footer
			v-if="props.leftFooterButton.name && props.rightFooterButton.name">
			<ion-toolbar>
				<div class="footer-buttons">
					<ion-button
						expand="block"
						class="footer-btn"
						:color="leftFooterButton.color"
						@click="$emit('leftFooterButtonClicked')">
						<ion-icon v-if="leftFooterButton.icon" :icon="leftFooterButton.icon" slot="start"></ion-icon>
						{{ leftFooterButton.name }}
					</ion-button>
					<ion-button
						expand="block"
						class="footer-btn"
						:color="rightFooterButton.color"
						:disabled="rightFooterButton.disabled"
						@click="$emit('rightFooterButtonClicked')">
						<ion-icon v-if="rightFooterButton.icon" :icon="rightFooterButton.icon" slot="start"></ion-icon>
						{{ rightFooterButton.name }}
					</ion-button>
				</div>
			</ion-toolbar>
		</ion-footer>

		<ion-fab
			v-if="addButtonPath"
			vertical="bottom"
			horizontal="end"
			slot="fixed">
			<ion-fab-button @click="navigateTo(addButtonPath)">
				<ion-icon :icon="addOutline"></ion-icon>
			</ion-fab-button>
		</ion-fab>
	</ion-page>
</template>

<script setup lang="ts">
import {
	IonPage,
	IonHeader,
	IonToolbar,
	IonTitle,
	IonButtons,
	IonButton,
	IonIcon,
	IonContent,
	IonFab,
	IonFabButton,
	IonFooter,
} from '@ionic/vue';
import { personCircleOutline, addOutline } from 'ionicons/icons';
import { useRouter } from 'vue-router';

const props = defineProps({
	showProfileButton: {
		type: Boolean,
		default: true,
	},
	addButtonPath: {
		type: String,
		default: undefined,
	},
	leftFooterButton: {
		type: Object as () => { 
			name?: string; 
			color?: string; 
			path?: string; 
			icon?: any; 
			disabled?: boolean 
		},
		default: () => ({}),
	},
	rightFooterButton: {
		type: Object as () => { 
			name?: string; 
			color?: string; 
			path?: string; 
			icon?: any; 
			disabled?: boolean 
		},
		default: () => ({}),
	},
	headline: {
		type: String,
		default: '',
	},
});

defineEmits(['leftFooterButtonClicked', 'rightFooterButtonClicked']);

const router = useRouter();

const navigateTo = (link: string) => {
	if (link) router.push(link);
};
</script>

<style scoped>
ion-toolbar {
	height: 66px;
	display: flex;
	align-items: center;
}

ion-title {
	display: flex;
	align-items: center;
	height: 100%;
	cursor: pointer;
}

.template-headline {
	text-align: center;
	font-size: 1.3rem;
	font-weight: 600;
	margin: 16px 0 8px 0;
}

.footer-buttons {
	display: flex;
	width: 100%;
}

.footer-btn {
	flex: 1 1 50%;
	margin: 0 4px;
}
</style>
