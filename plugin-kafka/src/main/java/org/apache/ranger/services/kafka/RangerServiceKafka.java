/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.ranger.services.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ranger.authorization.hadoop.config.RangerConfiguration;
import org.apache.ranger.plugin.model.RangerPolicy;
import org.apache.ranger.plugin.model.RangerService;
import org.apache.ranger.plugin.model.RangerServiceDef;
import org.apache.ranger.plugin.service.RangerBaseService;
import org.apache.ranger.plugin.service.ResourceLookupContext;
import org.apache.ranger.services.kafka.client.ServiceKafkaClient;
import org.apache.ranger.services.kafka.client.ServiceKafkaConnectionMgr;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.apache.ranger.plugin.policyengine.RangerPolicyEngine.GROUP_PUBLIC;

public class RangerServiceKafka extends RangerBaseService {
	private static final Log LOG = LogFactory.getLog(RangerServiceKafka.class);

	public RangerServiceKafka() {
		super();
	}

	@Override
	public void init(RangerServiceDef serviceDef, RangerService service) {
		super.init(serviceDef, service);
	}

	@Override
	public Map<String, Object> validateConfig() throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();

		if (LOG.isDebugEnabled()) {
			LOG.debug("==> RangerServiceKafka.validateConfig(" + serviceName + ")");
		}

		if (configs != null) {
			try {
				ret = ServiceKafkaConnectionMgr.connectionTest(serviceName, configs);
			} catch (Exception e) {
				LOG.error("<== RangerServiceKafka.validateConfig Error:" + e);
				throw e;
			}
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("<== RangerServiceKafka.validateConfig(" + serviceName + "): ret=" + ret);
		}

		return ret;
	}

	@Override
	public List<String> lookupResource(ResourceLookupContext context) throws Exception {
		List<String> ret = null;

		if (LOG.isDebugEnabled()) {
			LOG.debug("==> RangerServiceKafka.lookupResource(" + serviceName + ")");
		}

		if (configs != null) {
			ServiceKafkaClient serviceKafkaClient = ServiceKafkaConnectionMgr.getKafkaClient(serviceName, configs);

			ret = serviceKafkaClient.getResources(context);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("<== RangerServiceKafka.lookupResource(" + serviceName + "): ret=" + ret);
		}

		return ret;
	}

	@Override
	public List<RangerPolicy> getDefaultRangerPolicies() throws Exception {

		if (LOG.isDebugEnabled()) {
			LOG.debug("==> RangerServiceKafka.getDefaultRangerPolicies() ");
		}

		List<RangerPolicy> ret = super.getDefaultRangerPolicies();

		String authType = RangerConfiguration.getInstance().get(RANGER_AUTH_TYPE,"simple");

		if (StringUtils.equalsIgnoreCase(authType, KERBEROS_TYPE)) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Auth type is " + KERBEROS_TYPE);
			}
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Auth type is " + authType);
			}
			for (RangerPolicy defaultPolicy : ret) {
				if(defaultPolicy.getName().contains("all")){
					for (RangerPolicy.RangerPolicyItem defaultPolicyItem : defaultPolicy.getPolicyItems()) {
						defaultPolicyItem.getGroups().add(GROUP_PUBLIC);
					}
				}
			}
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("<== RangerServiceKafka.getDefaultRangerPolicies() ");
		}
		return ret;
	}
}
