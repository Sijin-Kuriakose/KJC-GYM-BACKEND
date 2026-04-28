////
////Conversation opened. 1 read message.
////
////Skip to content
////Using Kristu Jayanti College Mail with screen readers
////
////reference handler for susbscription with webclient
////
////Joyal Saji <joyalsaji@kristujayanti.com>
////Mon, May 5, 5:27 PM (22 hours ago)
////to me
//
//
//public class CreatePaymentHandler implements Handler<RoutingContext> {
//    private static final Logger LOGGER = LoggerFactory.getLogger(CreatePaymentHandler.class);
//    private final ApplicantFeeService applicantFeeService;
//    private final WebClient webClient;
//    private final Redis redisCommandConnection;
//    private final String redisHashKey;
//
//    public CreatePaymentHandler(ApplicantFeeService applicantFeeService, Redis redisCommandConnection, String redisHashKey, WebClient webClient) {
//        this.applicantFeeService = applicantFeeService;
//        this.webClient = webClient;
//        this.redisCommandConnection = redisCommandConnection;
//        this.redisHashKey = redisHashKey;
//    }
//
//
//    @Override
//    public void handle(RoutingContext routingContext) {
//        // extract the  request body and response
//        JsonObject requestBody = routingContext.body().asJsonObject();
//        HttpServerResponse response = routingContext.response();
//        // Extract logged-in user information
//        String loggedInUserEmail = UserInfoUtil.getLoggedInUserEmail(routingContext.request().headers());
//        String loggedInUserInfo = UserInfoUtil.getLoggedInUserId(routingContext.request().headers());
//        // check user info is there
//        if (loggedInUserEmail == null) {
//            LOGGER.warn("Access denied: User information is missing");
//            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.UNAUTHORIZED, new JsonArray(), new JsonArray().add(new JsonObject().put("message", "User information is missing")));
//            return;
//        }
//        // Define required fields for validation
//        List<String> requiredFields = Arrays.asList(
//                APPLICATION_NUMBER.getPropertyName(),
//                LIST_OF_FEE_STRUCTURE.getPropertyName(),
//                PROGRAM_NAME.getPropertyName(),
//                EDUCATION_TYPE.getPropertyName(),
//                BATCH_CODE.getPropertyName(),
//                APPLICANT_AUTH_OBJECTID.getPropertyName()
//        );
//        // Parse and validate the request document
//        Document paramsDocument = Document.parse(requestBody.toString());
//        JsonArray validationResponse = DocumentParser.validateAndCleanDocument(paramsDocument, requiredFields);
//        // Check validation response
//        if (!validationResponse.isEmpty()) {
//            ResponseUtil.createResponse(response, ResponseType.VALIDATION, StatusCode.BAD_REQUEST, new JsonArray(), validationResponse);
//            return;
//        }
//
//        //creating order for fee settings
//        try {
//            // get the notes details from request body
//            Document notes = new Document();
//            notes.put(APPLICATION_NUMBER.getPropertyName(), paramsDocument.getString(APPLICATION_NUMBER.getPropertyName()));
//            notes.put(EDUCATION_TYPE.getPropertyName(), paramsDocument.getString(EDUCATION_TYPE.getPropertyName()));
//            notes.put(BATCH_CODE.getPropertyName(), paramsDocument.getString(BATCH_CODE.getPropertyName()));
//            notes.put(PROGRAM_NAME.getPropertyName(), paramsDocument.getString(PROGRAM_NAME.getPropertyName()));
//            notes.put(APPLICANT_AUTH_OBJECTID.getPropertyName(), ObjectIdUtil.convertObjectIdToString(paramsDocument.getObjectId(APPLICANT_AUTH_OBJECTID.getPropertyName())));
//
//
//            // process the fee payment request
//            Document processedApplicantPaymentRequest = this.applicantFeeService.processApplicantPaymentRequest(paramsDocument, loggedInUserInfo);
//            if (processedApplicantPaymentRequest != null && !processedApplicantPaymentRequest.isEmpty()) {
//                // save the fee payment request to database and return the result
//                Future<ObjectId> feePaymentRequestFuture = applicantFeeService.saveFeePaymentRequestFuture(processedApplicantPaymentRequest);
//                feePaymentRequestFuture.onComplete(ar -> {
//
//                    if (ar.succeeded() && ar.result() != null) {
//                        ObjectId feePaymentRequestId = ar.result();
//                        // define the payload for webclient api request
//                        Future<Document> feePaymentResponseFuture = this.applicantFeeService.fetchFeePaymentResponseFuture(feePaymentRequestId);
//
//                        feePaymentResponseFuture.onComplete(futureResponse -> {
//                            if (futureResponse.succeeded() && futureResponse.result() != null) {
//                                Document request = futureResponse.result();
//                                // payload
//                                Document requestData = new Document(request);
//                                requestData.put(FEE_PAYMENT_ORDER_ID.getPropertyName(), ObjectIdUtil.convertObjectIdToString(requestData.getObjectId("_id")));
//                                requestData.putAll(paramsDocument);
//                                JsonObject jsonPayload = new JsonObject(requestData.toJson());
//                                Document jsonDocument = Document.parse(jsonPayload.encode()); // Convert back to Document
//
//// Handle metadata processing
//                                Document metaData = new Document();
//                                if (jsonDocument.containsKey(FEE_COLLECTION_META_DATA.getPropertyName())) {
//                                    Document existingMetaData = jsonDocument.get(FEE_COLLECTION_META_DATA.getPropertyName(), Document.class);
//
//                                    if (existingMetaData != null && existingMetaData.containsKey(FEE_COLLECTION_INSTRUMENT_DETAILS.getPropertyName())) {
//                                        List<Document> feeCollectionDetails = existingMetaData.getList(FEE_COLLECTION_INSTRUMENT_DETAILS.getPropertyName(), Document.class);
//
//                                        for (Document feeCollection : feeCollectionDetails) {
//                                            if (feeCollection.containsKey(FEE_GROUP_OID.getPropertyName())) {
//                                                String feeGroupId = ObjectIdUtil.convertObjectIdToString(feeCollection.getObjectId(FEE_GROUP_OID.getPropertyName()));
//                                                feeCollection.put(FEE_GROUP_OID.getPropertyName(), feeGroupId); // Update converted ID
//                                            }
//
//                                            this.applicantFeeService.fetchEmployeeName(loggedInUserEmail).onComplete(employeeNameAr -> {
//                                                if (employeeNameAr.succeeded()) {
//                                                    String employeeFullName = employeeNameAr.result();
//                                                    if (employeeFullName != null) {
//                                                        feeCollection.put(EMPLOYEE_FULL_NAME.getPropertyName(), employeeFullName);
//                                                        LOGGER.info("Fetched the  fee collector name :{}", employeeFullName);
//                                                    }
//                                                } else {
//                                                    LOGGER.warn("Failed to fetch employee name for email: {}", loggedInUserEmail);
//                                                }
//                                                feeCollection.put(FEE_COLLECTED_STAFF_EMAIL.getPropertyName(), loggedInUserEmail);
//                                                feeCollection.put(FEE_COLLECTED_USER_ID.getPropertyName(), loggedInUserInfo);
//
//                                            });
//                                        }
//                                    }
//
//                                    metaData.putAll(existingMetaData);
//                                }
//
//                                jsonPayload.put(FEE_COLLECTION_META_DATA.getPropertyName(), metaData);
//                                notes.put("displayTotalAmount", requestData.getLong("payingTotalAmount"));
//                                jsonPayload.put(NOTES, notes);
//                                // webclient API
//                                RedisAPI redis = RedisAPI.api(redisCommandConnection);
//                                redis.hget(redisHashKey, "payment-server-config").onComplete(configAr -> {
//                                    if (configAr.succeeded()) {
//                                        String paymentServerConfig = configAr.result().toString();
//                                        if (paymentServerConfig != null && !paymentServerConfig.isEmpty()) {
//                                            try {
//                                                JsonObject config = new JsonObject(paymentServerConfig);
//
//                                                String ip = config.getString("ip");
//                                                int port = config.getInteger("port");
//                                                String createFeeSettingsOrderRoot = config.getString("create_fee_settings_order_root");
//                                                Future<JsonObject> jsonResponse = CreateHttpClientUtil.sendJsonPostRequest(
//                                                        webClient,
//                                                        ip,
//                                                        createFeeSettingsOrderRoot,
//                                                        jsonPayload,
//                                                        port
//                                                );
//
//                                                jsonResponse.onComplete(result -> {
//                                                    if (result.succeeded()) {
//                                                        JsonObject resultData = result.result();
//                                                        JsonObject responseData = resultData.getJsonObject("data")
//                                                                .getJsonObject("responseData");
//
//                                                        // Directly send the response data back to the UI
//                                                        ResponseUtil.createResponse(
//                                                                response,
//                                                                ResponseType.SUCCESS,
//                                                                StatusCode.TWOHUNDRED,
//                                                                new JsonArray().add(responseData.getJsonArray("data") != null && !responseData.getJsonArray("data").isEmpty() ? responseData.getJsonArray("data").getJsonObject(0) : new JsonObject()),
//                                                                new JsonArray().add(responseData.getJsonArray("message") != null && !responseData.getJsonArray("message").isEmpty() ? responseData.getJsonArray("message").getString(0) : new JsonObject())
//                                                        );
//
//                                                    } else {
//                                                        LOGGER.error("Operation failed: " + result.cause().getMessage());
//                                                        ResponseUtil.createResponse(
//                                                                response,
//                                                                ResponseType.ERROR,
//                                                                StatusCode.INTERNAL_SERVER_ERROR,
//                                                                new JsonArray(),
//                                                                new JsonArray().add("Failed to process request")
//                                                        );
//                                                    }
//                                                });
//                                            } catch (DecodeException e) {
//                                                LOGGER.error("Error parsing payment server config JSON: " + e.getMessage());
//                                                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
//                                                        new JsonArray(), new JsonArray().add("Error parsing payment server configuration"));
//                                            }
//                                        } else {
//                                            LOGGER.error("Payment server config not found or empty");
//                                            ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
//                                                    new JsonArray(), new JsonArray().add("Payment server configuration not found"));
//                                        }
//                                    } else {
//                                        LOGGER.error("Error fetching payment-server-config from Redis: {}", ar.cause().getMessage());
//                                        ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.INTERNAL_SERVER_ERROR,
//                                                new JsonArray(), new JsonArray().add("Error fetching payment server configuration"));
//                                    }
//                                });
//
//
//                            }
//                        });
//                    }
//                });
//
//            } else {
//                LOGGER.error(" Fee Payment Request is null");
//                ResponseUtil.createResponse(response, ResponseType.ERROR, StatusCode.BAD_REQUEST,
//                        new JsonArray(), new JsonArray().add("Fee Payment Details is Empty"));
//
//            }
//
//        } catch (Exception e) {
//            LOGGER.error("Error while Creating fee settings payment :{}", e.getMessage(), e);
//            throw new RuntimeException("Failed to generate fee setting order", e);
//        }
//    }
//
//
//}
//--
//Logo
//Website	www.kristujayanti.edu.in
//Phone	080 2846 5353
//Joyal Saji
//Software Development Engineer
//        KJSDC
//Kristu Jayanti College (Autonomous)
//NAAC A++ (CGPA 3.78/4) | NIRF 60
//Bengaluru, Karnataka, India - 560077
//FacebookLinkedInInstagramTwitter
