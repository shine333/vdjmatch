/*
 * Copyright 2013-{year} Mikhail Shugay (mikhail.shugay@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.antigenomics.vdjdb.stat

import com.antigenomics.vdjdb.impl.ClonotypeDatabase
import com.antigenomics.vdjtools.io.InputStreamFactory
import com.antigenomics.vdjtools.io.SampleStreamConnection
import org.junit.Test

import java.util.zip.GZIPInputStream

import static com.antigenomics.vdjdb.Util.resourceAsStream

class ClonotypeSearchSummaryTest {
    @Test
    void test() {
        def database = new ClonotypeDatabase(resourceAsStream("vdjdb_legacy.meta.txt"))

        database.addEntries(resourceAsStream("vdjdb_legacy.txt"))

        def sample = SampleStreamConnection.load([
                create: {
                    new GZIPInputStream(resourceAsStream("sergey_anatolyevich.gz"))
                },
                getId : { "sergey_anatolyevich.gz" }
        ] as InputStreamFactory)

        def results = database.search(sample)

        def colNames = ["origin", "disease.type", "disease", "source"]
        def summary = new ClonotypeSearchSummary(results, sample,
                colNames, database)

        assert summary.totalCounter.unique > 0

        //println summary.fieldCounters

        // we only have viral infection in legacy db
        assert summary.getCounter("disease.type", "infection").unique ==
                summary.getCounter("disease", "viral").unique

        assert summary.getCounter("source", "EBV").unique > 0
        assert summary.getCounter("source", "CMV").unique > 0
    }
}
