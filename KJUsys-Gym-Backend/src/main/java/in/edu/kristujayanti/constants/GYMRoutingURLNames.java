package in.edu.kristujayanti.constants;

/**
 * GYMRoutingURLNames interface defines constants for various routing URL paths used in the application.
 * These constants can be used throughout the application to ensure consistency and avoid hardcoding URL paths.
 */
public interface GYMRoutingURLNames {

    // add route endpoints here

    // Wildcard URL path for general API routing
    String API_WILDCARD_URL = "*";
    public static final  String SEND_OTP_URL="/send-otp";
    public static final  String SEND_VERIFY_OTP_URL="/verify-otp";

    public static final  String USER_URL="/user-register";
    public static final  String SUBSCRIPTION_URL="/user-subscription";
    public static final  String STATUS_URL="/update-user-status";
    public static final  String GET_SUBSCRIPTION_END_DATE_URL="/user-subscription";
    public static final String UPDATE_USER_URL="/user-update";

//  public static final  String SEND_OTP_URL="/api/sendotp";
//  public static final  String SEND_VERIFY_OTP_URL="/api/verifyotp";
    String CREATE_SERVICE_URL = "/services";
    String VIEW_SERVICES_URL = "/services";
    String VIEW_SERVICE_BY_ID_URL = "/services/:id";
    String UPDATE_SERVICE_URL = "/services/:id";
    //String DELETE_SERVICE_URL = "/services/:id";
    String EXPORT_SERVICES_URL = "/services/export";
    String CREATE_ADDON_URL = "/addons";
    String VIEW_ADDONS_URL = "/addons";
    String VIEW_ADDON_BY_ID_URL ="/addons/:addonId";
    String UPDATE_ADDON_URL="/addons/:addonId";
    String DELETE_ADDON_URL="/addons/:addonId";
    String EXPORT_ADDON_URL="/addons/export";
    String GET_PAYMENT_REPORT_URL ="/payment-report";
    String GET_USER_REPORT_URL="/user-report";

    String GYM_CHECKINOUT_URL = "/attendance";
    String VIEW_DASHBOARD_URL ="/dashboard";

    String VERIFY_GYM_PAYMENT ="/verify-gym-payment";

    //String GET_REPORT_URL="/reports";





}