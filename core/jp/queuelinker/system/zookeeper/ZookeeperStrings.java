/* Copyright 2013 Yamana Laboratory, Waseda University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.queuelinker.system.zookeeper;

/**
 *
 */
public class ZookeeperStrings {

    /**
     * @return
     */
    public static String topPath() {
        return "/QueueLinker";
    }

    public static String registrationPath() {
        return topPath() + "/registration";
    }

    public static String masterSignaturePath() {
        return topPath() + "/master";
    }

    public static String clientTopPath() {
        return topPath() + "/clients";
    }

    public static String clientRegistrationPath() {
        return registrationPath() + "/clients";
    }

    public static String clientPath(final int clientId) {
        return clientTopPath() + "/" + String.format("%010d", clientId);
    }

    public static String clientRequestPath(final int clientId) {
        return clientPath(clientId) + "/requests";
    }

    public static String clientRequestAckTopPath(final int clientId) {
        return clientPath(clientId) + "/acks";
    }

    public static String clientRequestAckPath(final int clientId, final long requestId) {
        return clientRequestAckTopPath(clientId) + "/" + String.format("%020d", requestId);
    }

    public static String clientHeartPath(final int clientId) {
        return clientPath(clientId) + "/heart";
    }

    public static String clientSignaturePath(final int clientId) {
        return clientPath(clientId) + "/signature";
    }

    public static String workerTopPath() {
        return topPath() + "/workers";
    }

    public static String workerRegistrationPath() {
        return registrationPath() + "/workers";
    }

    public static String workerPath(final int workerId) {
        return workerTopPath() + "/" + String.format("%010d", workerId);
    }

    public static String workerOrderPath(final int workerId) {
        return workerPath(workerId) + "/orders";
    }

    public static String workerRequestPath(final int workerId) {
        return workerPath(workerId) + "/requests";
    }

    public static String workerHeartPath(final int workerId) {
        return workerPath(workerId) + "/heart";
    }

    public static String workerHeartRegex() {
        return workerTopPath() + "/*/heart";
    }

    public static String workerSignaturePath(final int workerId) {
        return workerPath(workerId) + "/signature";
    }

    public static String workerSystemInfoPath(final int workerId) {
        return workerPath(workerId) + "/systemInfo";
    }

    public static String atomicPath() {
        return topPath() + "/atomic";
    }

    public static String nextServerIdPath() {
        return atomicPath() + "/nextServerId";
    }

    public static String nextClientIdPath() {
        return atomicPath() + "/nextClientId";
    }

    public static String nextJobIdPath() {
        return atomicPath() + "/nextJobId";
    }

    public static String jobPath() {
        return topPath() + "/jobs";
    }

    public static String jobTopPath() {
        return jobPath() + "/current";
    }

    public static String jobPath(final long jobId) {
        return jobTopPath() + "/" + String.format("%010d", jobId);
    }

    public static String jobStatsTopPath(final long jobId) {
        return jobPath(jobId) + "/stats";
    }

    public static String jobStatsPath(final long jobId, final int workerId) {
        return jobStatsTopPath(jobId) + String.format("/%010d", workerId);
    }

    public static String jobDescPath(final long jobId) {
        return jobPath(jobId) + "/job";
    }

    public static String jobScheduledGraphPath(final long jobId) {
        return jobPath(jobId) + "/graph";
    }

    public static String jobVerticesPath(final long jobId) {
        return jobPath(jobId) + "/vertices";
    }

    public static String jobStatusPath(final long jobId) {
        return jobPath(jobId) + "/status";
    }

    public static String jobVertexInfoPath(final long jobId, final int vertexId) {
        return jobVerticesPath(jobId) + String.format("/%010d", vertexId);
    }

    public static String jobReadyCountdownLatch(final long jobId) {
        return jobPath(jobId) + "/readycount";
    }

    public static String finishedJobTopPath() {
        return jobPath() + "/finished";
    }
}
