<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-buttons slot="start">
          <ion-back-button default-href="/items"></ion-back-button>
        </ion-buttons>
        <ion-title>Neuer Eintrag</ion-title>
      </ion-toolbar>
    </ion-header>
    <ion-content>
      <ion-list>
        <!-- Hier wird das Formular für neue Einträge eingefügt -->
      </ion-list>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonList, IonButtons, IonBackButton } from '@ionic/vue';
</script> 