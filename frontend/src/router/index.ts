import { createRouter, createWebHashHistory, RouteRecordRaw } from "vue-router";
import HomePage from "@/views/HomePage/HomePage.vue";
import ItemsOverviewPage from "@/views/ItemPages/ItemsOverviewPage.vue";
import ReportsOverviewPage from "@/views/ReportPages/ReportsOverviewPage.vue";
import ReportDetailsPage from "@/views/ReportPages/ReportDetailsPage.vue";
import EditReportPage from "@/views/ReportPages/EditReportPage.vue";
import LocationsOverviewPage from "@/views/LocationsPages/LocationsOverviewPage.vue";
import ReportItemPage from "@/views/ItemPages/ReportItemPage.vue";
import ItemDetailsPage from "@/views/ItemPages/ItemDetailsPage.vue";
import EditItemPage from "@/views/ItemPages/EditItemPage.vue";
import AddLocationPage from "@/views/LocationsPages/AddLocationPage.vue";
import LocationDetailsPage from "@/views/LocationsPages/LocationDetailsPage.vue";
import EditLocationPage from "@/views/LocationsPages/EditLocationPage.vue";
import UserProfilePage from "@/views/UserPages/UserProfilePage.vue";

const routes: RouteRecordRaw[] = [
  {
    path: "/",
    name: "home",
    component: HomePage,
  },
  {
    path: "/items",
    redirect: "/items/overview",
  },
  {
    path: "/items/overview",
    name: "items-overview",
    component: ItemsOverviewPage,
  },
  {
    path: "/items/report",
    name: "report-item",
    component: ReportItemPage,
  },
  {
    path: "/items/:id",
    name: "item-details",
    component: ItemDetailsPage,
  },
  {
    path: "/items/:id/edit",
    name: "edit-item",
    component: EditItemPage,
  },
  {
    path: "/reports",
    component: ReportsOverviewPage,
  },
  {
    path: "/reports/overview",
    name: "reports-overview",
    component: ReportsOverviewPage,
  },
  {
    path: "/reports/:id",
    name: "report-details",
    component: ReportDetailsPage,
  },
  {
    path: "/reports/:id/edit",
    name: "edit-report",
    component: EditReportPage,
  },
  {
    path: "/locations",
    redirect: "/locations/overview",
  },
  {
    path: "/locations/overview",
    name: "locations-overview",
    component: LocationsOverviewPage,
  },
  {
    path: "/locations/add",
    name: "add-location",
    component: AddLocationPage,
  },
  {
    path: "/locations/:id",
    name: "location-details",
    component: LocationDetailsPage,
  },
  {
    path: "/locations/:id/edit",
    name: "edit-location",
    component: EditLocationPage,
  },
  {
    path: "/user",
    name: "user-profile",
    component: UserProfilePage,
  },
];

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
