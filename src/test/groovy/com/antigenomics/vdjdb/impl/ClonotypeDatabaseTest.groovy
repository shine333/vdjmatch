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

package com.antigenomics.vdjdb.impl

import com.antigenomics.vdjdb.TestUtil
import com.antigenomics.vdjdb.sequence.SearchScope
import com.antigenomics.vdjtools.io.InputStreamFactory
import com.antigenomics.vdjtools.io.SampleStreamConnection
import org.junit.Test

import java.util.zip.GZIPInputStream

import static com.antigenomics.vdjdb.TestUtil.TEST_SAMPLE
import static com.antigenomics.vdjdb.Util.resourceAsStream

class ClonotypeDatabaseTest {

    @Test
    void loadTest() {
        def database = new ClonotypeDatabase(resourceAsStream("vdjdb_legacy.meta.txt"))

        database.addEntries(resourceAsStream("vdjdb_legacy.txt"))

        database.columns.each { assert !it.values.empty }
    }

    @Test
    void sampleTest() {
        def database = new ClonotypeDatabase(resourceAsStream("vdjdb_legacy.meta.txt"))

        database.addEntries(resourceAsStream("vdjdb_legacy.txt"))

        def sample = SampleStreamConnection.load([
                create: {
                    new GZIPInputStream(resourceAsStream("sergey_anatolyevich.gz"))
                },
                getId : { "sergey_anatolyevich.gz" }
        ] as InputStreamFactory)

        def results = database.search(sample)

        def lapgatResults = results.find { it.key.cdr3aa == "CASSLAPGATNEKLFF" }

        assert lapgatResults.value.size() > 0

        assert lapgatResults.value[0].row[TestUtil.SOURCE_COL].value == "CMV"
    }

    @Test
    void sampleTest2() {
        def database = new ClonotypeDatabase(resourceAsStream("vdjdb_legacy.meta.txt"),
                false, false, new SearchScope(3, 1, 3),
                ScoringProvider.loadVdjamScoring(0)
        )

        database.addEntries(resourceAsStream("vdjdb_legacy.txt"))

        assert database.search(TEST_SAMPLE).size() > 0
    }
}
