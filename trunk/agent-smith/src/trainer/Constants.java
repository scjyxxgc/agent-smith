package trainer;

import java.lang.annotation.RetentionPolicy;

import edu.umich.eecs.tac.props.Query;

public class Constants
{
	public enum LogQueryType {
		n_n(null, null),
		n_d(null, "dvd"), 
		n_t(null, "tv"), 
		n_a(null, "audio"), 
		l_n("lioneer", null), 
		p_n("pg", null), 
		f_n("flat", null), 
		l_d("lioneer", "dvd"), 
		l_t("lioneer", "tv"), 
		l_a("lioneer", "audio"), 
		p_d("pg", "dvd"), 
		p_t("pg", "tv"), 
		p_a("pg", "audio"), 
		f_d("flat", "dvd"), 
		f_t("flat", "tv"), 
		f_a("flat", "audio");

		private String manufacturer;
		private String component;

		private LogQueryType(String manufact, String compo){
			this.manufacturer = manufact;
			this.component = compo;
		}
		
		public Query toQuery(){
			return new Query(this.getManufacturer(), this.getComponent());
		}
		
		public String getManufacturer(){
			return this.manufacturer;
		}
		
		public String getComponent(){
			return this.component;
		}
	}
	
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
