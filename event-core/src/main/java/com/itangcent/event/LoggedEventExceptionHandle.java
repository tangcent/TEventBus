package com.itangcent.event;


import com.itangcent.event.utils.ExceptionUtils;

import java.text.MessageFormat;
import java.util.logging.Logger;

public abstract class LoggedEventExceptionHandle implements SubscriberExceptionHandler {

    static final LoggedEventExceptionHandle TRACED_LOGGED_EVENT_EXCEPTION_HANDLE = new TracedLoggedEventExceptionHandle();

    static final LoggedEventExceptionHandle MESSAGE_LOGGED_EVENT_EXCEPTION_HANDLE = new MessageLoggedEventExceptionHandle();

    public static LoggedEventExceptionHandle traced() {
        return TRACED_LOGGED_EVENT_EXCEPTION_HANDLE;
    }

    public static LoggedEventExceptionHandle message() {
        return MESSAGE_LOGGED_EVENT_EXCEPTION_HANDLE;
    }

    private static Logger logger(SubscriberExceptionContext context) {
        return Logger.getLogger(EventBus.class.getName() + "." + context.getEventBus().name());
    }

    @Override
    public void handleException(Throwable exception, SubscriberExceptionContext context) {
        Logger logger = logger(context);
        logger.severe(message(exception, context));
    }

    protected abstract String message(Throwable exception, SubscriberExceptionContext context);

    private static class MessageLoggedEventExceptionHandle extends LoggedEventExceptionHandle {

        @Override
        protected String message(Throwable exception, SubscriberExceptionContext context) {
            return "Exception [" +
                    exception.getMessage()
                    + "] thrown by subscriber ["
                    + context.getSubscriber()
                    + "] on eventBus ["
                    + context.getEventBus().name()
                    + "] when dispatching event: ["
                    + context.getEvent()
                    + "]";
        }
    }

    private static class TracedLoggedEventExceptionHandle extends LoggedEventExceptionHandle {
        @Override
        protected String message(Throwable exception, SubscriberExceptionContext context) {
            return MessageFormat.format("\r\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" +
                            "\r\nEventBus:" +
                            "\r\n-------------------" +
                            "\r\n{0}" +
                            "\r\n-------------------" +
                            "\r\nSubscriber:" +
                            "\r\n-------------------" +
                            "\r\n{1}" +
                            "\r\n-------------------" +
                            "\r\nEvent:" +
                            "\r\n-------------------" +
                            "\r\n{2}" +
                            "\r\n-------------------" +
                            "\r\nRetry:" +
                            "\r\n-------------------" +
                            "\r\n{3}" +
                            "\r\n-------------------" +
                            "\r\nError:" +
                            "\r\n-------------------" +
                            "\r\n'{'\r\n\ttype:{4}" +
                            "\r\n\tmessage:{5}" +
                            "\r\n\tstackTrace:{6}" +
                            "\r\n'}'" +
                            "\r\n-------------------" +
                            "\r\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>",
                    context.getEventBus().name(),
                    context.getSubscriber(),
                    context.getEvent(),
                    context.getRetriedTimes(),
                    exception.getClass().getName(),
                    exception.getMessage(),
                    ExceptionUtils.getStackTrace(exception));
        }
    }

}
