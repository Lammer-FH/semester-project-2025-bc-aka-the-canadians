import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { reportService } from "@/services";
import { Report, ReportCreateData, ReportUpdateData } from "@/models/report";

export const useReportStore = defineStore("report", () => {
    const reports = ref<Report[]>([]);
    const currentReport = ref<Report | null>(null);
    const loading = ref<boolean>(false);
    const error = ref<string | null>(null);

    const getReports = computed(() => reports.value);
    const getCurrentReport = computed(() => currentReport.value);
    const isLoading = computed(() => loading.value);
    const getError = computed(() => error.value);

    // Derived/computed reports
    const getResolvedReports = computed(() =>
        reports.value.filter(report =>
            report.items && report.items.length > 0 &&
            report.items.every(item => item.claimedByUserId !== null && item.claimedByUserId !== undefined)
        )
    );
    const getOpenReports = computed(() =>
        reports.value.filter(report =>
            !report.items || report.items.length === 0 ||
            report.items.some(item => item.claimedByUserId === null || item.claimedByUserId === undefined)
        )
    );

    const fetchReports = async () => {
        try {
            loading.value = true;
            error.value = null;
            const response = await reportService.getAllReports();
            reports.value = Array.isArray(response) ? response : [];
        } catch (err) {
            error.value =
                err instanceof Error ? err.message : "Failed to fetch reports";
            reports.value = [];
            console.error("Error fetching reports:", err);
        } finally {
            loading.value = false;
        }
    };

    const fetchReportById = async (id: number) => {
        try {
            loading.value = true;
            currentReport.value = await reportService.getReportById(id);
        } catch (err) {
            error.value =
                err instanceof Error ? err.message : "An error occurred";
        } finally {
            loading.value = false;
        }
    };

    const createReport = async (reportData: ReportCreateData) => {
        try {
            loading.value = true;
            const newReport = await reportService.createReport(reportData);
            reports.value.push(newReport);
            return newReport;
        } catch (err) {
            error.value =
                err instanceof Error ? err.message : "An error occurred";
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const updateReport = async (id: number, reportData: ReportUpdateData) => {
        try {
            loading.value = true;
            const updatedReport = await reportService.updateReport(
                id,
                reportData,
            );
            const index = reports.value.findIndex((report) => report.id === id);
            if (index !== -1) {
                reports.value[index] = updatedReport;
            }
            return updatedReport;
        } catch (err) {
            error.value =
                err instanceof Error ? err.message : "An error occurred";
            throw err;
        } finally {
            loading.value = false;
        }
    };

    const deleteReport = async (id: number) => {
        try {
            loading.value = true;
            await reportService.deleteReport(id);
            reports.value = reports.value.filter((report) => report.id !== id);
        } catch (err) {
            error.value =
                err instanceof Error ? err.message : "An error occurred";
            throw err;
        } finally {
            loading.value = false;
        }
    };

    return {
        reports,
        currentReport,
        loading,
        error,
        getReports,
        getCurrentReport,
        isLoading,
        getError,
        fetchReports,
        fetchReportById,
        createReport,
        updateReport,
        deleteReport,
        getResolvedReports,
        getOpenReports,
    };
});
