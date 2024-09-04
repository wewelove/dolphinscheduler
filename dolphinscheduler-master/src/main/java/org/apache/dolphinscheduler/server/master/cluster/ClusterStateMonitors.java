/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dolphinscheduler.server.master.cluster;

import org.apache.dolphinscheduler.server.master.engine.system.SystemEventBus;
import org.apache.dolphinscheduler.server.master.engine.system.event.MasterFailoverEvent;
import org.apache.dolphinscheduler.server.master.engine.system.event.WorkerFailoverEvent;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClusterStateMonitors {

    @Autowired
    private ClusterManager clusterManager;

    @Autowired
    private SystemEventBus systemEventBus;

    public void start() {
        clusterManager.getMasterClusters()
                .registerListener((IClusters.ServerRemovedListener<MasterServerMetadata>) this::masterRemoved);
        clusterManager.getWorkerClusters()
                .registerListener((IClusters.ServerRemovedListener<WorkerServerMetadata>) this::workerRemoved);
        log.info("ClusterStateMonitors started...");
    }

    void masterRemoved(MasterServerMetadata masterServer) {
        systemEventBus.publish(MasterFailoverEvent.of(masterServer.getAddress(), new Date()));
    }

    void workerRemoved(WorkerServerMetadata workerServer) {
        systemEventBus.publish(WorkerFailoverEvent.of(workerServer.getAddress(), new Date()));
    }

}