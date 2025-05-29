<template>
	<template-page
		:headline="'Gegenstand melden'"
		:leftFooterButton="leftFooterButton"
		:rightFooterButton="rightFooterButton"
		@leftFooterButtonClicked="handleCancel"
		@rightFooterButtonClicked="handleSubmit">
		<div class="form-container">
			<!-- Progress Section -->
			<div class="progress-section">
				<div class="progress-bar">
					<div
						class="progress-fill"
						:style="{ width: `${completionPercentage}%` }"></div>
				</div>
				<p class="progress-text">{{ completionPercentage }}% ausgefüllt</p>
			</div>

			<!-- Form Header -->
			<div class="form-header">
				<ion-icon :icon="megaphoneOutline" class="header-icon"></ion-icon>
				<h2>Neuen Bericht erstellen</h2>
				<p>
					Hilf anderen dabei, verlorene Gegenstände wiederzufinden oder melde
					einen Fund
				</p>
			</div>

			<!-- Form Content -->
			<div class="form-content">
				<!-- Report Type Selection -->
				<div class="input-group">
					<ion-item
						class="modern-item"
						:class="{
							'item-filled': reportData.type,
							'item-error': errors.type,
						}">
						<ion-label position="stacked" class="custom-label">
							<ion-icon :icon="flagOutline" class="label-icon"></ion-icon>
							Art des Berichts *
						</ion-label>
						<ion-select
							v-model="reportData.type"
							placeholder="Wähle eine Option"
							interface="popover"
							class="custom-select"
							@selectionChange="validateField('type')">
							<ion-select-option value="LOST">
								<div class="select-option">
									<ion-icon
										:icon="searchOutline"
										class="option-icon"></ion-icon>
									<div>
										<strong>Verlustbericht</strong>
										<p>Ich habe einen Gegenstand verloren</p>
									</div>
								</div>
							</ion-select-option>
							<ion-select-option value="FOUND">
								<div class="select-option">
									<ion-icon :icon="eyeOutline" class="option-icon"></ion-icon>
									<div>
										<strong>Fundbericht</strong>
										<p>Ich habe einen Gegenstand gefunden</p>
									</div>
								</div>
							</ion-select-option>
						</ion-select>
					</ion-item>
					<div v-if="errors.type" class="error-message">
						<ion-icon :icon="alertCircleOutline"></ion-icon>
						{{ errors.type }}
					</div>
				</div>

				<!-- Item Name -->
				<div class="input-group">
					<ion-item
						class="modern-item"
						:class="{
							'item-filled': reportData.itemName,
							'item-error': errors.itemName,
						}">
						<ion-label position="stacked" class="custom-label">
							<ion-icon :icon="textOutline" class="label-icon"></ion-icon>
							Name des Gegenstands *
						</ion-label>
						<ion-input
							v-model="reportData.itemName"
							placeholder="z.B. iPhone 14, Schlüsselbund, Rucksack..."
							class="custom-input"
							@ionBlur="validateField('itemName')"></ion-input>
					</ion-item>
					<div v-if="errors.itemName" class="error-message">
						<ion-icon :icon="alertCircleOutline"></ion-icon>
						{{ errors.itemName }}
					</div>
				</div>

				<!-- Description -->
				<div class="input-group">
					<ion-item
						class="modern-item textarea-item"
						:class="{
							'item-filled': reportData.description,
						}">
						<ion-label position="stacked" class="custom-label">
							<ion-icon
								:icon="documentTextOutline"
								class="label-icon"></ion-icon>
							Beschreibung
						</ion-label>
						<ion-textarea
							v-model="reportData.description"
							placeholder="Beschreibe den Gegenstand detailliert: Farbe, Größe, besondere Merkmale, wo du ihn verloren/gefunden hast..."
							class="custom-textarea"
							rows="4"></ion-textarea>
					</ion-item>
				</div>

				<!-- Location -->
				<div class="input-group">
					<ion-item
						class="modern-item"
						:class="{
							'item-filled': reportData.location,
							'item-error': errors.location,
						}">
						<ion-label position="stacked" class="custom-label">
							<ion-icon :icon="locationOutline" class="label-icon"></ion-icon>
							Standort *
						</ion-label>
						<ion-select
							v-model="reportData.location"
							placeholder="Wähle einen Standort"
							interface="popover"
							class="custom-select"
							@selectionChange="validateField('location')">
							<ion-select-option
								v-for="location in availableLocations"
								:key="location.id"
								:value="location.name">
								{{ location.name }} - {{ location.building }}, Etage
								{{ location.floor }}
							</ion-select-option>
							<ion-select-option value="other">
								<strong
									>Anderer Standort (bitte in Beschreibung angeben)</strong
								>
							</ion-select-option>
						</ion-select>
					</ion-item>
					<div v-if="errors.location" class="error-message">
						<ion-icon :icon="alertCircleOutline"></ion-icon>
						{{ errors.location }}
					</div>
				</div>

				<!-- Reporter Information -->
				<div class="input-group">
					<h3 class="section-title">
						<ion-icon :icon="personOutline" class="section-icon"></ion-icon>
						Deine Kontaktinformationen
					</h3>
					<p class="section-description">
						Damit andere dich kontaktieren können, wenn sie deinen Gegenstand
						gefunden haben.
					</p>

					<ion-item
						class="modern-item"
						:class="{
							'item-filled': reportData.reporterName,
							'item-error': errors.reporterName,
						}">
						<ion-label position="stacked" class="custom-label">
							<ion-icon :icon="personOutline" class="label-icon"></ion-icon>
							Dein Name *
						</ion-label>
						<ion-input
							v-model="reportData.reporterName"
							placeholder="Vor- und Nachname"
							class="custom-input"
							@ionBlur="validateField('reporterName')"></ion-input>
					</ion-item>
					<div v-if="errors.reporterName" class="error-message">
						<ion-icon :icon="alertCircleOutline"></ion-icon>
						{{ errors.reporterName }}
					</div>

					<ion-item
						class="modern-item"
						:class="{
							'item-filled': reportData.contactInfo,
						}">
						<ion-label position="stacked" class="custom-label">
							<ion-icon :icon="mailOutline" class="label-icon"></ion-icon>
							Kontakt (optional)
						</ion-label>
						<ion-input
							v-model="reportData.contactInfo"
							placeholder="E-Mail oder Telefon"
							class="custom-input"></ion-input>
					</ion-item>
				</div>

				<!-- Image Upload Section -->
				<div class="input-group">
					<div class="image-upload-section">
						<h3 class="upload-title">
							<ion-icon :icon="cameraOutline" class="title-icon"></ion-icon>
							Foto hinzufügen (optional)
						</h3>
						<p class="upload-description">
							Ein Foto hilft anderen, den Gegenstand zu identifizieren
						</p>

						<div v-if="imagePreview" class="image-preview-container">
							<img :src="imagePreview" alt="Vorschau" class="image-preview" />
							<ion-button
								fill="clear"
								color="danger"
								class="remove-image-btn"
								@click="removeImage">
								<ion-icon :icon="trashOutline" slot="icon-only"></ion-icon>
							</ion-button>
						</div>

						<div v-else class="upload-buttons">
							<ion-button
								expand="block"
								fill="outline"
								class="upload-btn"
								@click="takePhoto">
								<ion-icon :icon="cameraOutline" slot="start"></ion-icon>
								Foto aufnehmen
							</ion-button>
							<ion-button
								expand="block"
								fill="outline"
								class="upload-btn"
								@click="triggerFileInput">
								<ion-icon :icon="cloudUploadOutline" slot="start"></ion-icon>
								Foto hochladen
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

				<!-- Tips Section -->
				<div class="tips-section">
					<h3 class="tips-title">
						<ion-icon :icon="bulbOutline" class="title-icon"></ion-icon>
						Tipps für einen guten Bericht
					</h3>
					<div class="tips-list">
						<div class="tip-item">
							<ion-icon
								:icon="checkmarkCircleOutline"
								color="success"></ion-icon>
							<div class="tip-text">
								<p>
									<strong>Sei detailliert:</strong> Je mehr Details, desto
									besser können andere helfen
								</p>
							</div>
						</div>
						<div class="tip-item">
							<ion-icon
								:icon="checkmarkCircleOutline"
								color="success"></ion-icon>
							<div class="tip-text">
								<p>
									<strong>Füge ein Foto hinzu:</strong> Bilder helfen enorm bei
									der Identifikation
								</p>
							</div>
						</div>
						<div class="tip-item">
							<ion-icon
								:icon="checkmarkCircleOutline"
								color="success"></ion-icon>
							<div class="tip-text">
								<p>
									<strong>Wähle den richtigen Standort:</strong> Wo hast du den
									Gegenstand zuletzt gesehen?
								</p>
							</div>
						</div>
						<div class="tip-item">
							<ion-icon
								:icon="checkmarkCircleOutline"
								color="success"></ion-icon>
							<div class="tip-text">
								<p>
									<strong>Bleib erreichbar:</strong> Gib korrekte Kontaktdaten
									an
								</p>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
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
	IonSelect,
	IonSelectOption,
	IonButton,
	IonProgressBar,
} from '@ionic/vue';
import {
	megaphoneOutline,
	textOutline,
	documentTextOutline,
	locationOutline,
	flagOutline,
	alertCircleOutline,
	informationCircleOutline,
	checkmarkCircleOutline,
	closeCircleOutline,
	cameraOutline,
	cloudUploadOutline,
	trashOutline,
	bulbOutline,
	personOutline,
	mailOutline,
	searchOutline,
	eyeOutline,
} from 'ionicons/icons';
import { ref, computed, watch, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useItemStore } from '@/stores/itemStore';
import { useLocationStore } from '@/stores/locationStore';
import type { Item } from '@/models/item';

const router = useRouter();
const itemStore = useItemStore();
const locationStore = useLocationStore();

// Report data structure
const reportData = ref({
	type: '', // 'LOST' or 'FOUND'
	itemName: '',
	description: '',
	location: '',
	reporterName: '',
	contactInfo: '',
	imageData: '',
});

const errors = ref({
	type: '',
	itemName: '',
	location: '',
	reporterName: '',
});

const isSubmitting = computed(() => itemStore.isLoading);
const imagePreview = ref('');
const fileInput = ref<HTMLInputElement>();
const availableLocations = computed(() => locationStore.getLocations || []);

const leftFooterButton = computed(() => ({
	name: 'Abbrechen',
	color: 'medium',
	icon: closeCircleOutline,
}));

const rightFooterButton = computed(() => ({
	name: isSubmitting.value
		? 'Bericht wird gesendet...'
		: isValid.value
		? 'Bericht einreichen'
		: 'Felder ausfüllen',
	color: isValid.value ? 'primary' : 'medium',
	icon: checkmarkCircleOutline,
	disabled: !isValid.value || isSubmitting.value,
}));

const isValid = computed(() => {
	return (
		reportData.value.type.trim() !== '' &&
		reportData.value.itemName.trim() !== '' &&
		reportData.value.location.trim() !== '' &&
		reportData.value.reporterName.trim() !== ''
	);
});

const completionPercentage = computed(() => {
	const requiredFields = ['type', 'itemName', 'location', 'reporterName'];
	const optionalFields = ['description', 'contactInfo'];

	const requiredFilled = requiredFields.filter(
		(field) =>
			reportData.value[field as keyof typeof reportData.value].trim() !== ''
	).length;

	const optionalFilled = optionalFields.filter(
		(field) =>
			reportData.value[field as keyof typeof reportData.value].trim() !== ''
	).length;

	// Required fields count as 80%, optional as 20%
	const requiredWeight = 0.8;
	const optionalWeight = 0.2;

	const requiredScore =
		(requiredFilled / requiredFields.length) * requiredWeight;
	const optionalScore =
		(optionalFilled / optionalFields.length) * optionalWeight;

	return Math.round((requiredScore + optionalScore) * 100);
});

// Load locations when component mounts
onMounted(async () => {
	await locationStore.fetchLocations();
});

const validateField = (fieldName: keyof typeof errors.value) => {
	const value = String(
		reportData.value[fieldName as keyof typeof reportData.value] || ''
	).trim();

	switch (fieldName) {
		case 'type':
			errors.value.type = !value ? 'Bitte wähle die Art des Berichts aus' : '';
			break;
		case 'itemName':
			if (!value) {
				errors.value.itemName = 'Name des Gegenstands ist erforderlich';
			} else if (value.length < 2) {
				errors.value.itemName = 'Name muss mindestens 2 Zeichen lang sein';
			} else {
				errors.value.itemName = '';
			}
			break;
		case 'location':
			errors.value.location = !value ? 'Bitte wähle einen Standort aus' : '';
			break;
		case 'reporterName':
			if (!value) {
				errors.value.reporterName = 'Dein Name ist erforderlich';
			} else if (value.length < 2) {
				errors.value.reporterName = 'Name muss mindestens 2 Zeichen lang sein';
			} else {
				errors.value.reporterName = '';
			}
			break;
	}
};

const validateAllFields = () => {
	validateField('type');
	validateField('itemName');
	validateField('location');
	validateField('reporterName');
};

const handleCancel = () => {
	router.back();
};

const handleSubmit = async () => {
	if (!isValid.value) {
		validateAllFields();
		return;
	}

	try {
		// Create enhanced description with reporter info
		const enhancedDescription = [
			reportData.value.description,
			'',
			`--- Berichtinformationen ---`,
			`Berichtstyp: ${
				reportData.value.type === 'LOST' ? 'Verlustbericht' : 'Fundbericht'
			}`,
			`Gemeldet von: ${reportData.value.reporterName}`,
			reportData.value.contactInfo
				? `Kontakt: ${reportData.value.contactInfo}`
				: '',
			`Berichtsdatum: ${new Date().toLocaleDateString('de-DE')}`,
		]
			.filter(Boolean)
			.join('\n');

		// Map report data to item structure for backend
		const itemData: Omit<Item, 'id' | 'createdAt' | 'updatedAt'> = {
			name: reportData.value.itemName,
			description: enhancedDescription,
			location: reportData.value.location,
			status: reportData.value.type, // 'LOST' or 'FOUND'
			imageData: imagePreview.value,
		};

		// Create the item (which represents our "report")
		const newItem = await itemStore.createItem(itemData);

		if (newItem) {
			// Show success and navigate
			console.log('Report submitted successfully:', newItem);
			// TODO: Show success toast: "Bericht erfolgreich eingereicht!"

			// Navigate to the new "report" (item details)
			router.push(`/items/${newItem.id}`);
		}
	} catch (error) {
		console.error('Error submitting report:', error);
		// TODO: Show error toast
	}
};

// Image handling functions
const triggerFileInput = () => {
	fileInput.value?.click();
};

const handleFileSelect = (event: Event) => {
	const target = event.target as HTMLInputElement;
	const file = target.files?.[0];

	if (file) {
		// Validate file type
		if (!file.type.startsWith('image/')) {
			// TODO: Show error toast
			console.error('Please select an image file');
			return;
		}

		// Validate file size (max 5MB)
		const maxSize = 5 * 1024 * 1024; // 5MB
		if (file.size > maxSize) {
			// TODO: Show error toast
			console.error('File size must be less than 5MB');
			return;
		}

		// Convert to base64 for preview and storage
		const reader = new FileReader();
		reader.onload = (e) => {
			const result = e.target?.result as string;
			imagePreview.value = result;
			reportData.value.imageData = result;
		};
		reader.readAsDataURL(file);
	}
};

const takePhoto = async () => {
	try {
		// For now, just trigger file input (in a real app, you'd use camera API)
		triggerFileInput();
	} catch (error) {
		console.error('Error taking photo:', error);
		// TODO: Show error toast
	}
};

const removeImage = () => {
	imagePreview.value = '';
	reportData.value.imageData = '';
	if (fileInput.value) {
		fileInput.value.value = '';
	}
};

// Watch for changes and clear errors
watch(
	() => reportData.value.type,
	() => {
		if (errors.value.type) validateField('type');
	}
);

watch(
	() => reportData.value.itemName,
	() => {
		if (errors.value.itemName) validateField('itemName');
	}
);

watch(
	() => reportData.value.location,
	() => {
		if (errors.value.location) validateField('location');
	}
);

watch(
	() => reportData.value.reporterName,
	() => {
		if (errors.value.reporterName) validateField('reporterName');
	}
);
</script>

<style scoped>
.form-container {
	padding: 20px;
	max-width: 600px;
	margin: 0 auto;
}

.progress-section {
	margin-bottom: 30px;
	text-align: center;
}

.progress-bar {
	width: 100%;
	height: 8px;
	background: var(--ion-color-light-shade);
	border-radius: 4px;
	overflow: hidden;
	margin-bottom: 8px;
}

.progress-fill {
	height: 100%;
	background: linear-gradient(
		90deg,
		var(--ion-color-primary),
		var(--ion-color-primary-shade)
	);
	border-radius: 4px;
	transition: width 0.3s ease;
}

.progress-text {
	font-size: 0.85em;
	color: var(--ion-color-medium);
	font-weight: 600;
	margin: 0;
}

.form-header {
	text-align: center;
	margin-bottom: 40px;
}

.header-icon {
	font-size: 56px;
	color: var(--ion-color-primary);
	margin-bottom: 16px;
}

.form-header h2 {
	color: var(--ion-color-dark);
	margin: 0 0 8px 0;
	font-weight: 700;
	font-size: 1.8em;
}

.form-header p {
	color: var(--ion-color-medium);
	margin: 0 0 24px 0;
	font-size: 1em;
	line-height: 1.4;
}

.form-content {
	animation: slideInUp 0.6s ease-out;
}

.input-group {
	margin-bottom: 24px;
}

.section-title {
	color: var(--ion-color-dark);
	margin: 0 0 8px 0;
	font-size: 1.1em;
	font-weight: 600;
	display: flex;
	align-items: center;
	gap: 8px;
}

.section-icon {
	font-size: 18px;
	color: var(--ion-color-primary);
}

.section-description {
	color: var(--ion-color-medium);
	margin: 0 0 16px 0;
	font-size: 0.9em;
	line-height: 1.4;
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
	transform: translateY(-2px);
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
	font-size: 18px;
	color: var(--ion-color-primary);
}

.custom-textarea {
	min-height: 80px;
	resize: vertical;
}

.textarea-item {
	--padding-bottom: 16px;
}

.select-option {
	display: flex;
	align-items: center;
	gap: 12px;
	padding: 8px 0;
}

.option-icon {
	font-size: 20px;
	color: var(--ion-color-primary);
}

.select-option div {
	flex: 1;
}

.select-option p {
	margin: 2px 0 0 0;
	font-size: 0.85em;
	color: var(--ion-color-medium);
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

/* Image Upload Styles */
.image-upload-section {
	background: var(--ion-color-light-tint);
	border: 2px dashed var(--ion-color-light-shade);
	border-radius: 12px;
	padding: 20px;
	text-align: center;
	transition: all 0.3s ease;
}

.image-upload-section:hover {
	border-color: var(--ion-color-primary-tint);
	background: rgba(var(--ion-color-primary-rgb), 0.02);
}

.upload-title {
	color: var(--ion-color-dark);
	margin: 0 0 8px 0;
	font-size: 1.1em;
	font-weight: 600;
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 8px;
}

.title-icon {
	font-size: 20px;
	color: var(--ion-color-primary);
}

.upload-description {
	color: var(--ion-color-medium);
	margin: 0 0 20px 0;
	font-size: 0.9em;
}

.image-preview-container {
	position: relative;
	display: inline-block;
	margin-bottom: 16px;
}

.image-preview {
	max-width: 200px;
	max-height: 200px;
	border-radius: 8px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.remove-image-btn {
	position: absolute;
	top: -8px;
	right: -8px;
	--background: var(--ion-color-danger);
	--color: white;
	--border-radius: 50%;
	width: 32px;
	height: 32px;
}

.upload-buttons {
	display: flex;
	flex-direction: column;
	gap: 12px;
	max-width: 300px;
	margin: 0 auto;
}

.upload-btn {
	--border-width: 2px;
	--border-style: solid;
	font-weight: 500;
}

/* Tips Section */
.tips-section {
	background: linear-gradient(
		135deg,
		var(--ion-color-primary-tint),
		rgba(var(--ion-color-primary-rgb), 0.1)
	);
	border-radius: 12px;
	padding: 20px;
	margin-top: 32px;
	border-left: 4px solid var(--ion-color-primary);
}

.tips-title {
	color: var(--ion-color-dark);
	margin: 0 0 16px 0;
	font-size: 1.1em;
	font-weight: 600;
	display: flex;
	align-items: center;
	gap: 8px;
}

.tips-list {
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.tip-item {
	display: flex;
	align-items: flex-start;
	gap: 12px;
}

.tip-text p {
	margin: 0;
	color: var(--ion-color-medium-shade);
	font-size: 0.9em;
	line-height: 1.4;
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

/* Responsive Design */
@media (max-width: 768px) {
	.form-container {
		padding: 16px;
	}

	.form-header {
		margin-bottom: 32px;
	}

	.header-icon {
		font-size: 48px;
	}

	.form-header h2 {
		font-size: 1.6em;
	}

	.upload-buttons {
		max-width: 100%;
	}
}

@media (max-width: 480px) {
	.progress-section {
		margin-bottom: 24px;
	}

	.form-header {
		margin-bottom: 24px;
	}

	.input-group {
		margin-bottom: 20px;
	}

	.tips-section {
		margin-top: 24px;
		padding: 16px;
	}
}
</style>
