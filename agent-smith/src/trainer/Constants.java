package trainer;

public class Constants
{
	public enum LogQueryType {n_n, n_d, n_t, n_a, l_n, p_n, f_n, l_d, l_t, l_a, p_d, p_t, p_a, f_d, f_t, f_a};
	public enum LogProduct {l_d, l_t, l_a, p_d, p_t, p_a, f_d, f_t, f_a, Null};
	public enum LogQueryReportParams {ad, promotedImpressions, regularImpressions, clicks, cpc, position};
	public enum LogSalesReportParams {impressions, revenue}
	public enum LogBidBundleReportParams {ad, bid, limit}
	public enum LogRetailCatalogReportParams {salesProfit}
	public enum LogSlotInfoReportParams {promotedSlots, regularSlots, promotedSlotBonus}
	public enum LogUserClickModelReportParams {advertiserEffect, continuationProbability}
	public enum LogReserveInfoReportParams {promotedReserve, regularReserve}
	public enum LogPublisherInfoReportParams {squashingParameter}
}
