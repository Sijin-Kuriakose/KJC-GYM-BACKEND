package in.edu.kristujayanti.router;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import in.edu.kristujayanti.constants.CommonKeys;
import in.edu.kristujayanti.constants.ContextRoutingURLName;
import in.edu.kristujayanti.handlers.GymSybscriptionPayment.GymVerifyPaymentHandler;
import in.edu.kristujayanti.handlers.otp.OTPHandler;
//import in.edu.kristujayanti.handlers.handler.RegisterHandler;
//import in.edu.kristujayanti.handlers.handler.payments.PaymentHandler;
import in.edu.kristujayanti.handlers.GymSybscriptionPayment.SubscriptionHandler;
import in.edu.kristujayanti.handlers.userhandlers.UpdateUserHandler;
import in.edu.kristujayanti.handlers.userhandlers.UpdateUserStatusHandler;
import in.edu.kristujayanti.handlers.userhandlers.UserHandler;
import in.edu.kristujayanti.handlers.otp.VerifyOTPHandler;
import in.edu.kristujayanti.services.UserServices.GymPaymentService;
import in.edu.kristujayanti.services.UserServices.OTPService;
//import in.edu.kristujayanti.services.service.RegisterService;
import in.edu.kristujayanti.services.UserServices.SubscriptionService;
import in.edu.kristujayanti.services.UserServices.UserService;
import in.edu.kristujayanti.handlers.*;
import in.edu.kristujayanti.services.*;
import in.edu.kristujayanti.constants.GYMRoutingURLNames;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.redis.client.Redis;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;



import static in.edu.kristujayanti.constants.GYMRoutingURLNames.*;

/**
 * GYMRouter sets up the main application routes and handlers.
 * It extends the RouterBase to utilize common properties and methods for routing.
 */
public class GYMRouter extends RouterBase {

    private final Vertx vertx;

    /**
     * Constructs a GYMRouter with necessary dependencies.
     *
     * @param router the Vert.x router
     * @param redisCommandConnection the Redis connection
     * @param mongoDatabase the MongoDB database
     * @param mongoClient the MongoDB client
     * @param client the Vert.x WebClient
     * @param vertx the Vert.x instance
     */
    public GYMRouter(Router router, Redis redisCommandConnection,String redisHashKey, MongoDatabase mongoDatabase, MongoClient mongoClient, WebClient client, Vertx vertx) {
        super(router, redisCommandConnection,redisHashKey, mongoDatabase, mongoClient, client);
        this.vertx = vertx;
    }

    /**
     * Sets up the application routes with handlers.
     */
    public void setUpRouters() {
        // Define allowed headers for CORS

        Set<String> allowHeaders = Stream.of(
                CommonKeys.CONTENT_TYPE,
                CommonKeys.X_AUTH_CORRELATION_ID,
                HttpHeaders.AUTHORIZATION.toString(),
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Methods",
                "Access-Control-Allow-Headers"
        ).collect(Collectors.toSet());

        // Define allowed HTTP methods for CORS
        Set<HttpMethod> allowMethods = Stream.of(
                HttpMethod.GET,
                HttpMethod.POST,
                HttpMethod.PUT
        ).collect(Collectors.toSet());

        // Setup CORS and Body Handlers
        this.router.route().handler(CorsHandler.create()
                .addOrigin("*")
                .allowCredentials(true)
                .allowedHeaders(allowHeaders)
                .allowedMethods(allowMethods));

        // add routes here (use addRoute())
        ServiceManager serviceManager = new ServiceManager(mongoDatabase, mongoClient);
        GymAttendanceManager attendanceManager=new GymAttendanceManager(mongoDatabase,mongoClient);
        AdminDashboardService dashboardService=new AdminDashboardService(mongoDatabase,mongoClient);
        GymPaymentService gymPaymentService= new GymPaymentService();

        addRoute(HttpMethod.POST, GYMRoutingURLNames.CREATE_SERVICE_URL, new InsertServiceHandler(serviceManager));
        addRoute(HttpMethod.GET, GYMRoutingURLNames.EXPORT_SERVICES_URL, new ExportServiceHandler(serviceManager));
        addRoute(HttpMethod.GET, GYMRoutingURLNames.VIEW_SERVICES_URL, new ViewServiceHandler(serviceManager));
        addRoute(HttpMethod.GET, GYMRoutingURLNames.VIEW_SERVICE_BY_ID_URL, new ViewServiceHandler(serviceManager));
        addRoute(HttpMethod.PUT, GYMRoutingURLNames.UPDATE_SERVICE_URL, new UpdateServiceHandler(serviceManager));
        // addRoute(HttpMethod.DELETE, GYMRoutingURLNames.DELETE_SERVICE_URL, new DeleteServiceHandler(serviceManager));

        addRoute(HttpMethod.POST, GYMRoutingURLNames.GYM_CHECKINOUT_URL, new AttendanceHandler(attendanceManager));
        //addRoute(HttpMethod.POST, GYMRoutingURLNames.GYM_CHECKOUT_URL, new CheckOutHandler(attendanceManager));
        addRoute(HttpMethod.POST, GYMRoutingURLNames.VIEW_DASHBOARD_URL, new AdminDashboardHandler(dashboardService));

        // Register route
//        RegisterService registerService = new RegisterService(mongoDatabase, mongoClient);
//        RegisterHandler registerHandler = new RegisterHandler (registerService);
//        addRoute(HttpMethod.POST, REGISTER_USER_URL, registerHandler::addMember);
        //user route
        UserService userService = new UserService(mongoDatabase,mongoClient);
        UserHandler userHandler = new UserHandler (userService);
        addRoute(HttpMethod.POST, USER_URL, userHandler::addMembership);
        UpdateUserHandler updateUserHandler=new UpdateUserHandler(userService);
        addRoute(HttpMethod.PUT,UPDATE_USER_URL,updateUserHandler::updateUserDetails);
        //subscription routes
        SubscriptionService subscriptionService = new SubscriptionService(mongoDatabase, mongoClient);
        SubscriptionHandler subscriptionHandler = new SubscriptionHandler(subscriptionService,redisCommandConnection,redisHashkey,client);
        addRoute(HttpMethod.POST, SUBSCRIPTION_URL, subscriptionHandler::handleSubscriptionRequest);
//        addRoute(HttpMethod.POST, SUBSCRIPTION_URL, subscriptionHandler::);
      addRoute(HttpMethod.GET, GET_SUBSCRIPTION_END_DATE_URL, subscriptionHandler::getCurrentSubscriptionDetails);
        // Status update route
        UpdateUserStatusHandler updateUserStatusHandler = new UpdateUserStatusHandler(subscriptionService);
        addRoute(HttpMethod.PUT, STATUS_URL, updateUserStatusHandler); // New route

        // OTP routes
        OTPService otpService = new OTPService(redisCommandConnection, vertx);
        OTPHandler otpHandler = new OTPHandler(otpService);
        addRoute(HttpMethod.POST, SEND_OTP_URL, otpHandler::sendOTP);
        //verify Otp routes
        VerifyOTPHandler verifyOTPHandler = new VerifyOTPHandler(otpService,userService);
        addRoute(HttpMethod.POST, SEND_VERIFY_OTP_URL, verifyOTPHandler::verifyOTP);

        addRoute(HttpMethod.POST, SEND_VERIFY_OTP_URL, verifyOTPHandler::verifyOTP);
        this.router.route().handler(context -> {
            System.out.println("Request: " + context.request().method() + " " + context.request().absoluteURI());
            context.next();
        });

//        VerifyOTPHandler verifyOTPHandler = new VerifyOTPHandler(otpService);
//        VerifyOTPHandler verifyOTPHandler = new VerifyOTPHandler(otpService);

        //payments
//        PaymentHandler paymentHandler = new PaymentHandler(subscriptionService);
//        addRoute(HttpMethod.POST, STATUS_URL, paymentHandler); // New route

        AddonService addonService = new AddonService(mongoDatabase, mongoClient);
        PaymentReport paymentReport = new PaymentReport(mongoDatabase, mongoClient);
        UserReport userReport= new UserReport(mongoDatabase, mongoClient);

        addRoute(HttpMethod.POST, CREATE_ADDON_URL, new CreateAddonHandler(addonService));
        addRoute(HttpMethod.GET, VIEW_ADDONS_URL, new ViewAddonHandler(addonService));
        addRoute(HttpMethod.GET,EXPORT_ADDON_URL, new ExportAddonHandler(addonService));
        addRoute(HttpMethod.GET, VIEW_ADDON_BY_ID_URL, new ViewAddonHandler(addonService));
        addRoute(HttpMethod.PUT, UPDATE_ADDON_URL, new UpdateAddonHandler(addonService));
        addRoute(HttpMethod.DELETE, DELETE_ADDON_URL, new DeleteAddonHandler(addonService));
        addRoute(HttpMethod.POST, GET_PAYMENT_REPORT_URL, new PaymentReportHandler(paymentReport));
        addRoute(HttpMethod.GET, GET_USER_REPORT_URL, new UserReportHandler(userReport));

        //verify  payment
        addRoute(HttpMethod.POST, VERIFY_GYM_PAYMENT, new GymVerifyPaymentHandler(gymPaymentService,redisCommandConnection,redisHashkey,client));

        this.router.route().handler(context -> {
            System.out.println("No route matched for: " + context.request().method() + " " + context.request().path());
            context.response()
                    .setStatusCode(404)
                    .putHeader("Content-Type", "application/json")
                    .end(new JsonObject()
                            .put("error", "Route not found")
                            .encode());
        });
    }

    /**
     * Helper method to add routes with the specified method, path, and handler.
     *
     * @param method the HTTP method
     * @param path the URL path
     * @param handler the request handler
     */
    private void addRoute(HttpMethod method, String path, Handler<RoutingContext> handler) {
        this.router.route(method, ContextRoutingURLName.GYM_CONTEXT_URL_NAME.concat(path))
                .handler(BodyHandler.create())
                .blockingHandler(handler);

    }
}
