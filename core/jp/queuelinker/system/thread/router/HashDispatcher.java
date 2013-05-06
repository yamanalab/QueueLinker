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

package jp.queuelinker.system.thread.router;

import jp.queuelinker.module.base.HashCoder;

public class HashDispatcher extends Dispatcher {

    private final HashCoder coder;

    private final DispatchRouteInformation[] routes;

    private final double[] partitioningRate;

    public HashDispatcher(final DispatchRouteInformation[] routes,
                          final double[] partitioningRate, final HashCoder coder, final boolean mutable) {
        super(mutable);
        this.routes = routes;
        this.partitioningRate = partitioningRate;
        this.coder = coder;
    }

    @Override
    void dispatch(final Object element) {
        final int hash = coder.itemHashCode(element);
        final int targetIndex = Math.abs(hash % routes.length);

        routes[targetIndex].accepter.dispatchAccept(element, routes[targetIndex]);
    }

    @Override
    void newAccepter(final DispatchAccepter accepter) {
        // TODO Auto-generated method stub
    }

    @Override
    void removeAccepter(final DispatchAccepter accepter) {
        // TODO Auto-generated method stub
    }
}
