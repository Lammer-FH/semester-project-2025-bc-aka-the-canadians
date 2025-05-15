<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-back-button default-href="/items"></ion-back-button>
        </ion-buttons>
        <ion-title>Details</ion-title>
      </ion-toolbar>
    </ion-header>
    <ion-content>
      <!-- Hier werden die Item-Details dynamisch eingefügt -->
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonButtons, IonBackButton } from '@ionic/vue';
</script> 