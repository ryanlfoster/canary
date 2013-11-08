package com.citytechinc.monitoring.constants;

import com.citytechinc.monitoring.services.persistence.RecordPersistenceServiceWrapper;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.util.Comparator;

/**
 *
 * @author CITYTECH, INC. 2013
 *
 */
public final class Constants {

    /**
     * Deny outside instantiation.
     */
    private Constants() {

    }

    public static final String CITYTECH_SERVICE_VENDOR_NAME = "CITYTECH, Inc.";

    public static final String SERVICE_MONITOR_DASHBOARD_PAGE_COMPONENT_PATH = "/apps/osgi-service-monitor/components/page/servicemonitordashboard";
    public static final String SERVICE_MONITOR_DASHBOARD_TEMPLATE_PATH = "/apps/osgi-service-monitor/templates/servicemonitordashboard";
    public static final String DASHBOARD_COMPONENT_PATH = "osgi-service-monitor/components/content/dashboard";

    public static final HashFunction FAST_HASH = Hashing.goodFastHash(8);

    public static final Comparator<RecordPersistenceServiceWrapper> RECORD_PERSISTENCE_SERVICE_WRAPPER_COMPARATOR = new Comparator<RecordPersistenceServiceWrapper>() {

        @Override
        public int compare(final RecordPersistenceServiceWrapper o1, final RecordPersistenceServiceWrapper o2) {

            final Integer o1Ranking = o1.getDefinition().ranking();
            final Integer o2Ranking = o2.getDefinition().ranking();

            return o1Ranking.compareTo(o2Ranking);
        }
    };

}
