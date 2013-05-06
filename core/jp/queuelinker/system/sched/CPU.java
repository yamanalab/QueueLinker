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

package jp.queuelinker.system.sched;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.queuelinker.system.thread.ThreadUnit;
import jp.queuelinker.system.util.ObjectLinkedListElement;

/**
 *
 */
public class CPU {

    /**
     *
     */
    private static final long serialVersionUID = -5599455226801140065L;

    /**
     *
     */
    private final int cpuChipId;

    /**
     *
     */
    private final int coreId;


    /**
     *
     */
    private final int affinityId;

    /**
     *
     */
    private final ArrayList<ThreadUnit> units = new ArrayList<ThreadUnit>();

    /**
     *
     */
    private final ObjectLinkedListElement<CPU> element = new ObjectLinkedListElement<CPU>(this);

    /**
     * @param cpuChipId
     * @param coreId
     * @param affinityId
     */
    public CPU(final int cpuChipId, final int coreId, final int affinityId) {
        this.cpuChipId = cpuChipId;
        this.coreId = coreId;
        this.affinityId = affinityId;
    }

    public void assigneThreadUnit(final ThreadUnit threadUnit) {
        units.add(threadUnit);
    }

    public void removeThreadUnit(final ThreadUnit threadUnit) {
        units.remove(threadUnit);
    }

    public int getCPUChipId() {
        return cpuChipId;
    }

    public int getCPUCoreId() {
        return coreId;
    }

    public int getAffinityId() {
        return affinityId;
    }

    public List<ThreadUnit> getAssignedThreadUnit() {
        return Collections.unmodifiableList(units);
    }

    public ObjectLinkedListElement<CPU> getListElement() {
        return element;
    }

    @Override
    public String toString() {
        return "CPU " + coreId;
    }
}
