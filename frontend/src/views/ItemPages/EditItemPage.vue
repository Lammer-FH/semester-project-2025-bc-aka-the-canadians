<template>
	<template-page
		:headline="'Gegenstand bearbeiten'"
		:leftFooterButton="leftFooterButton"
		:rightFooterButton="rightFooterButton"
		@leftFooterButtonClicked="handleCancel"
		@rightFooterButtonClicked="handleSave">
		<div class="form-container">
			<div v-if="isLoading" class="loading-container">
				<ion-spinner name="crescent" color="primary"></ion-spinner>
				<p>Lade Gegenstand...</p>
			</div>

			<div v-else-if="error && !item.name" class="empty-state">
				<ion-icon :icon="alertCircleOutline" class="empty-icon"></ion-icon>
				<h2>Fehler beim Laden</h2>
				<p>{{ error }}</p>
				<ion-button @click="loadItem">
					<ion-icon :icon="refreshOutline" slot="start"></ion-icon>
					Erneut versuchen
				</ion-button>
			</div>

			<div v-else class="form-content">
				<div class="form-header">
					<ion-icon :icon="createOutline" class="header-icon"></ion-icon>
					<h2>Gegenstand bearbeiten</h2>
					<p>Aktualisiere die Details für diesen Gegenstand</p>
					<div v-if="item.updatedAt" class="last-modified">
						<ion-icon :icon="timeOutline" class="time-icon"></ion-icon>
						Zuletzt bearbeitet: {{ formatDate(item.updatedAt) }}
					</div>
				</div>

				<div class="input-group">
					<ion-item
						class="modern-item"
						:class="{
							'item-error': errors.name,
							'item-filled': item.name,
						}">
						<ion-label position="stacked" class="custom-label">
							<ion-icon :icon="textOutline" class="label-icon"></ion-icon>
							Name des Gegenstands *
						</ion-label>
						<ion-input
							v-model="item.name"
							placeholder="z.B. Schwarzer Rucksack"
							@ionBlur="validateField('name')"
							:class="{ 'input-error': errors.name }"></ion-input>
					</ion-item>
					<div v-if="errors.name" class="error-message">
						<ion-icon :icon="alertCircleOutline"></ion-icon>
						{{ errors.name }}
					</div>
				</div>

				<div class="input-group">
					<ion-item
						class="modern-item textarea-item"
						:class="{
							'item-filled': item.description,
						}">
						<ion-label position="stacked" class="custom-label">
							<ion-icon
								:icon="documentTextOutline"
								class="label-icon"></ion-icon>
							Beschreibung
						</ion-label>
						<ion-textarea
							v-model="item.description"
							placeholder="Detaillierte Beschreibung des Gegenstands..."
							class="custom-textarea"
							:auto-grow="true"></ion-textarea>
					</ion-item>
				</div>

				<div class="input-group">
					<ion-item
						class="modern-item"
						:class="{
							'item-error': errors.location,
							'item-filled': item.location,
						}">
						<ion-label position="stacked" class="custom-label">
							<ion-icon :icon="locationOutline" class="label-icon"></ion-icon>
							Fundort/Verlustort *
						</ion-label>
						<ion-input
							v-model="item.location"
							placeholder="z.B. Bibliothek, Hörsaal A1, Mensa"
							@ionBlur="validateField('location')"
							:class="{ 'input-error': errors.location }"></ion-input>
					</ion-item>
					<div v-if="errors.location" class="error-message">
						<ion-icon :icon="alertCircleOutline"></ion-icon>
						{{ errors.location }}
					</div>
				</div>

				<div class="input-group">
					<ion-item
						class="modern-item"
						:class="{
							'item-error': errors.status,
							'item-filled': item.status,
						}">
						<ion-label position="stacked" class="custom-label">
							<ion-icon :icon="flagOutline" class="label-icon"></ion-icon>
							Status *
						</ion-label>
						<ion-select
							v-model="item.status"
							placeholder="Wähle den Status"
							@ionChange="validateField('status')">
							<ion-select-option value="LOST">Verloren</ion-select-option>
							<ion-select-option value="FOUND">Gefunden</ion-select-option>
							<ion-select-option value="CLAIMED">Abgeholt</ion-select-option>
						</ion-select>
					</ion-item>
					<div v-if="errors.status" class="error-message">
						<ion-icon :icon="alertCircleOutline"></ion-icon>
						{{ errors.status }}
					</div>
				</div>

				<div class="input-group">
					<div class="image-upload-section">
						<h4 class="upload-title">
							<ion-icon :icon="cameraOutline" class="title-icon"></ion-icon>
							Bild (optional)
						</h4>
						<p class="upload-description">
							Füge ein Bild hinzu, um den Gegenstand besser identifizieren zu
							können.
						</p>

						<div
							v-if="imagePreview || item.imageUrl"
							class="image-preview-container">
							<img
								:src="imagePreview || item.imageUrl"
								:alt="item.name"
								class="image-preview" />
							<ion-button
								fill="clear"
								color="danger"
								class="remove-image-btn"
								@click="removeImage">
								<ion-icon :icon="trashOutline" slot="icon-only"></ion-icon>
							</ion-button>
						</div>

						<div class="upload-buttons">
							<ion-button fill="outline" class="upload-btn" @click="takePhoto">
								<ion-icon :icon="cameraOutline" slot="start"></ion-icon>
								Foto aufnehmen
							</ion-button>
							<ion-button
								fill="outline"
								class="upload-btn"
								@click="triggerFileInput">
								<ion-icon :icon="cloudUploadOutline" slot="start"></ion-icon>
								Datei hochladen
							</ion-button>
						</div>

						<input
							ref="fileInput"
							type="file"
							accept="image/*"
							style="display: none"
							@change="handleFileSelect" />
					</div>
				</div>

				<div class="action-buttons">
					<ion-button
						fill="outline"
						color="danger"
						expand="block"
						class="delete-button"
						@click="handleDelete">
						<ion-icon :icon="trashOutline" slot="start"></ion-icon>
						Gegenstand löschen
					</ion-button>
				</div>

				<div class="form-footer-info">
					<ion-item class="info-item">
						<ion-icon
							:icon="informationCircleOutline"
							slot="start"
							color="primary"></ion-icon>
						<div class="info-text">
							<p>
								Alle Änderungen werden automatisch gespeichert, wenn du auf
								"Änderungen speichern" klickst.
							</p>
							<p>Mit * markierte Felder sind Pflichtfelder.</p>
						</div>
					</ion-item>
				</div>
			</div>
		</div>

		<ion-alert
			:is-open="showDeleteAlert"
			header="Gegenstand löschen"
			message="Bist du sicher, dass du diesen Gegenstand löschen möchtest? Diese Aktion kann nicht rückgängig gemacht werden."
			:buttons="alertButtons"
			@didDismiss="showDeleteAlert = false"></ion-alert>
	</template-page>
</template>

<script setup lang="ts">
import TemplatePage from '@/components/TemplatePage.vue';
import {
	IonItem,
	IonLabel,
	IonInput,
	IonTextarea,
	IonIcon,
	IonButton,
	IonSpinner,
	IonAlert,
	IonSelect,
	IonSelectOption,
} from '@ionic/vue';
import {
	createOutline,
	textOutline,
	documentTextOutline,
	locationOutline,
	flagOutline,
	alertCircleOutline,
	informationCircleOutline,
	checkmarkCircleOutline,
	closeCircleOutline,
	trashOutline,
	timeOutline,
	cameraOutline,
	cloudUploadOutline,
	refreshOutline,
} from 'ionicons/icons';
import { ref, computed, watch, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useItemStore } from '@/stores/itemStore';
import type { Item } from '@/models/item';

const router = useRouter();
const route = useRoute();
const itemStore = useItemStore();

const item = ref<Item>({
	id: 0,
	name: '',
	description: '',
	location: '',
	status: '',
	createdAt: '',
	updatedAt: '',
	imageUrl: '',
});

const errors = ref({
	name: '',
	location: '',
	status: '',
});

const isLoading = computed(() => itemStore.isLoading);
const error = computed(() => itemStore.getError);
const isSaving = ref(false);
const showDeleteAlert = ref(false);
const imagePreview = ref('');
const fileInput = ref<HTMLInputElement>();

const leftFooterButton = computed(() => ({
	name: 'Abbrechen',
	color: 'medium',
	icon: closeCircleOutline,
}));

const rightFooterButton = computed(() => ({
	name: isSaving.value
		? 'Speichere...'
		: isValid.value
		? 'Änderungen speichern'
		: 'Felder ausfüllen',
	color: isValid.value ? 'primary' : 'medium',
	icon: checkmarkCircleOutline,
	disabled: !isValid.value || isSaving.value,
}));

const isValid = computed(() => {
	return (
		item.value.name.trim() !== '' &&
		item.value.location.trim() !== '' &&
		item.value.status.trim() !== '' &&
		Object.values(errors.value).every((error) => error === '')
	);
});

const alertButtons = [
	{
		text: 'Abbrechen',
		role: 'cancel',
		cssClass: 'alert-button-cancel',
	},
	{
		text: 'Löschen',
		role: 'destructive',
		cssClass: 'alert-button-confirm',
		handler: () => confirmDelete(),
	},
];

const formatDate = (dateString: string) => {
	return new Date(dateString).toLocaleDateString('de-DE', {
		day: '2-digit',
		month: '2-digit',
		year: 'numeric',
		hour: '2-digit',
		minute: '2-digit',
	});
};

const validateField = (fieldName: keyof typeof errors.value) => {
	const value = String(
		item.value[fieldName as keyof typeof item.value] || ''
	).trim();

	switch (fieldName) {
		case 'name':
			if (!value) {
				errors.value.name = 'Name ist erforderlich';
			} else if (value.length < 2) {
				errors.value.name = 'Name muss mindestens 2 Zeichen haben';
			} else {
				errors.value.name = '';
			}
			break;
		case 'location':
			if (!value) {
				errors.value.location = 'Standort ist erforderlich';
			} else {
				errors.value.location = '';
			}
			break;
		case 'status':
			if (!value) {
				errors.value.status = 'Status ist erforderlich';
			} else {
				errors.value.status = '';
			}
			break;
	}
};

const validateAllFields = () => {
	validateField('name');
	validateField('location');
	validateField('status');
};

const loadItem = async () => {
	try {
		const itemId = Number(route.params.id);
		if (!itemId) {
			throw new Error('Invalid item ID');
		}

		await itemStore.fetchItemById(itemId);
		const loadedItem = itemStore.getCurrentItem;

		if (loadedItem) {
			item.value = { ...loadedItem };
		} else {
			throw new Error('Item not found');
		}
	} catch (error) {
		console.error('Error loading item:', error);
		router.back();
	}
};

const handleCancel = () => {
	router.back();
};

const handleSave = async () => {
	if (!isValid.value) {
		validateAllFields();
		return;
	}

	isSaving.value = true;

	try {
		const updateData: Partial<Item> = {
			name: item.value.name.trim(),
			description: item.value.description?.trim() || '',
			location: item.value.location.trim(),
			status: item.value.status,
		};

		if (imagePreview.value) {
			updateData.imageData = imagePreview.value;
		}

		await itemStore.updateItem(item.value.id, updateData);

		router.push(`/items/${item.value.id}`);
	} catch (error) {
		console.error('Error saving item:', error);
	} finally {
		isSaving.value = false;
	}
};

const handleDelete = () => {
	showDeleteAlert.value = true;
};

const confirmDelete = async () => {
	try {
		await itemStore.deleteItem(item.value.id);
		router.push('/items/overview');
	} catch (error) {
		console.error('Error deleting item:', error);
	}
};

const triggerFileInput = () => {
	fileInput.value?.click();
};

const handleFileSelect = (event: Event) => {
	const target = event.target as HTMLInputElement;
	const file = target.files?.[0];

	if (file) {
		if (!file.type.startsWith('image/')) {
			console.error('Please select an image file');
			return;
		}

		const maxSize = 5 * 1024 * 1024; // 5MB
		if (file.size > maxSize) {
			console.error('File size must be less than 5MB');
			return;
		}

		const reader = new FileReader();
		reader.onload = (e) => {
			imagePreview.value = e.target?.result as string;
		};
		reader.readAsDataURL(file);
	}
};

const takePhoto = async () => {
	try {
		triggerFileInput();
	} catch (error) {
		console.error('Error taking photo:', error);
	}
};

const removeImage = () => {
	imagePreview.value = '';
	item.value.imageUrl = '';
	if (fileInput.value) {
		fileInput.value.value = '';
	}
};

watch(
	() => item.value.name,
	() => {
		if (errors.value.name) errors.value.name = '';
	}
);

watch(
	() => item.value.location,
	() => {
		if (errors.value.location) errors.value.location = '';
	}
);

watch(
	() => item.value.status,
	() => {
		if (errors.value.status) errors.value.status = '';
	}
);

onMounted(() => {
	loadItem();
});
</script>

<style scoped>
.form-container {
	padding: 20px;
	max-width: 600px;
	margin: 0 auto;
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

.form-header {
	text-align: center;
	margin-bottom: 40px;
}

.header-icon {
	font-size: 48px;
	color: var(--ion-color-primary);
	margin-bottom: 16px;
}

.form-header h2 {
	color: var(--ion-color-dark);
	margin: 0 0 8px 0;
	font-weight: 600;
}

.form-header p {
	color: var(--ion-color-medium);
	margin: 0 0 16px 0;
	font-size: 0.95em;
}

.last-modified {
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 6px;
	background: var(--ion-color-light-tint);
	padding: 8px 16px;
	border-radius: 20px;
	font-size: 0.85em;
	color: var(--ion-color-medium);
	margin-top: 12px;
}

.time-icon {
	font-size: 14px;
}

.form-content {
	animation: slideInUp 0.6s ease-out;
}

.input-group {
	margin-bottom: 24px;
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

.modern-item.item-error {
	border-color: var(--ion-color-danger);
	--background: rgba(var(--ion-color-danger-rgb), 0.05);
}

.modern-item:focus-within {
	border-color: var(--ion-color-primary);
	box-shadow: 0 0 0 3px rgba(var(--ion-color-primary-rgb), 0.1);
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

.custom-textarea {
	min-height: 80px;
}

.textarea-item {
	--padding-bottom: 16px;
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

.action-buttons {
	margin: 40px 0;
	padding: 20px;
	border-top: 1px solid var(--ion-color-light-shade);
	border-bottom: 1px solid var(--ion-color-light-shade);
}

.delete-button {
	width: 100%;
	--border-radius: 12px;
	height: 48px;
	font-weight: 600;
}

.form-footer-info {
	margin-top: 20px;
	padding: 20px;
	background: var(--ion-color-light-tint);
	border-radius: 12px;
	border-left: 4px solid var(--ion-color-primary);
}

.info-item {
	--background: transparent;
	--padding-start: 0;
	--padding-end: 0;
}

.info-text p {
	margin: 0;
	color: var(--ion-color-medium);
	font-size: 0.9em;
}

.image-upload-section {
	background: var(--ion-color-light-tint);
	border-radius: 16px;
	padding: 20px;
	border: 2px dashed var(--ion-color-light-shade);
	transition: all 0.3s ease;
}

.image-upload-section:hover {
	border-color: var(--ion-color-primary-tint);
	background: rgba(var(--ion-color-primary-rgb), 0.02);
}

.upload-title {
	display: flex;
	align-items: center;
	gap: 8px;
	color: var(--ion-color-dark);
	margin: 0 0 8px 0;
	font-size: 1.1em;
	font-weight: 600;
}

.title-icon {
	font-size: 20px;
	color: var(--ion-color-primary);
}

.upload-description {
	color: var(--ion-color-medium);
	margin: 0 0 20px 0;
	font-size: 0.9em;
	line-height: 1.4;
}

.image-preview-container {
	position: relative;
	margin-bottom: 16px;
}

.image-preview {
	width: 100%;
	max-width: 300px;
	height: 200px;
	object-fit: cover;
	border-radius: 12px;
	border: 2px solid var(--ion-color-light-shade);
	display: block;
	margin: 0 auto;
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.remove-image-btn {
	position: absolute;
	top: 8px;
	right: 50%;
	transform: translateX(calc(50% - 150px));
	background: rgba(255, 255, 255, 0.9);
	border-radius: 50%;
	--padding-start: 8px;
	--padding-end: 8px;
	--padding-top: 8px;
	--padding-bottom: 8px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.upload-buttons {
	display: flex;
	gap: 12px;
	margin-bottom: 16px;
	justify-content: center;
}

.upload-btn {
	--border-radius: 12px;
	font-weight: 600;
	flex: 1;
	max-width: 200px;
}

@keyframes slideInUp {
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
	.form-container {
		padding: 16px;
	}

	.form-header {
		margin-bottom: 30px;
	}

	.header-icon {
		font-size: 40px;
	}

	.last-modified {
		padding: 6px 12px;
		font-size: 0.8em;
	}
}

@media (max-width: 480px) {
	.form-header {
		margin-bottom: 20px;
	}

	.input-group {
		margin-bottom: 20px;
	}

	.action-buttons {
		margin: 30px 0;
		padding: 16px;
	}
}
</style>
