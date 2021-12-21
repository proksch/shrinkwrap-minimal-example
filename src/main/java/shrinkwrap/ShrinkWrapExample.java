/*
 * Copyright 2021 Delft University of Technology
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package shrinkwrap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenResolvedArtifactImpl;
import org.jboss.shrinkwrap.resolver.impl.maven.ResolutionResult;

public class ShrinkWrapExample {

	private static final String SW_COORD = "org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-depchain:3.1.4:?";
	private static final String MY_COORD = "test:shrinkwrap";
//	private static final String MY_COORD = "test:shrinkwrap:0.0.1-SNAPSHOT";

	public void run() {

		var db = initDatabaseWithSomeCooridnates( //
				"org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-maven-archive:jar:3.1.4", //
				"org.arquillian.spacelift:arquillian-spacelift:jar:1.0.2", //
				"org.apache.maven.resolver:maven-resolver-transport-wagon:jar:1.4.1", //
//				"org.apache.maven.wagon:wagon-http-shared:jar:2.12", //
				"org.apache.maven:maven-resolver-provider:jar:3.6.3", //
				"org.apache.commons:commons-compress:jar:1.8.1", //
				"org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-spi-maven:jar:3.1.4", //
				"org.apache.maven.resolver:maven-resolver-impl:jar:1.4.1", //
				"org.codehaus.plexus:plexus-utils:jar:3.2.1", //
				"org.jboss.shrinkwrap:shrinkwrap-spi:jar:1.2.6", //
				"org.apache.maven.shared:maven-invoker:jar:3.0.0", //
//				"remla:mylib:jar:0.0.5", //
				"org.apache.maven:maven-model:jar:3.6.3", //
				"org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-spi:jar:3.1.4", //
//				"org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-depchain:pom:3.1.4", //
				"org.slf4j:slf4j-nop:jar:1.7.32", //
				"javax.inject:javax.inject:jar:1", //
				"org.apache.maven:maven-repository-metadata:jar:3.6.3", //
				"org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven-archive:jar:3.1.4", //
				"org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-maven-embedded:jar:3.1.4", //
				"org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven-embedded:jar:3.1.4", //
				"org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven:jar:3.1.4", //
				"org.codehaus.plexus:plexus-compiler-api:jar:2.7", //
				"org.eclipse.sisu:org.eclipse.sisu.plexus:jar:0.3.4", //
				"org.apache.maven:maven-model-builder:jar:3.6.3", //
				"org.apache.maven:maven-builder-support:jar:3.6.3", //
				"org.arquillian.spacelift:arquillian-spacelift-api:jar:1.0.2", //
				"org.apache.maven:maven-artifact:jar:3.6.3", //
				"org.apache.maven.wagon:wagon-file:jar:3.3.4", //
				"org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api:jar:3.1.4", //
				"org.apache.maven.wagon:wagon-http-lightweight:jar:2.12", //
				"org.jboss.shrinkwrap:shrinkwrap-api:jar:1.2.6", //
				"org.apache.maven.resolver:maven-resolver-spi:jar:1.4.1", //
				"org.jsoup:jsoup:jar:1.7.2", //
				"org.slf4j:slf4j-api:jar:1.7.32", //
				"org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-spi-maven-archive:jar:3.1.4", //
				"org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-maven:jar:3.1.4", //
				"org.apache.maven.resolver:maven-resolver-connector-basic:jar:1.4.1", //
				"commons-codec:commons-codec:jar:1.10", //
				"org.apache.maven.resolver:maven-resolver-util:jar:1.4.1", //
				"commons-io:commons-io:jar:2.5", //
				"org.apache.maven.wagon:wagon-provider-api:jar:3.3.4", //
				"org.apache.maven.resolver:maven-resolver-api:jar:1.4.1", //
				"org.codehaus.plexus:plexus-compiler-javac:jar:2.7", //
				"org.codehaus.plexus:plexus-interpolation:jar:1.25", //
				"org.jboss.shrinkwrap:shrinkwrap-impl-base:jar:1.2.6", //
				"org.eclipse.sisu:org.eclipse.sisu.inject:jar:0.3.4", //
				"org.apache.commons:commons-lang3:jar:3.8.1", //
				"org.codehaus.plexus:plexus-component-annotations:jar:1.7", //
				"org.sonatype.plexus:plexus-cipher:jar:1.4", //
				"org.apache.maven:maven-settings:jar:3.6.3", //
				"org.sonatype.plexus:plexus-sec-dispatcher:jar:1.4", //
				"org.apache.maven:maven-settings-builder:jar:3.6.3" //
		);

		var coordsToSources = new HashMap<String, String>();

		IntStream.range(0, 2).forEach(i -> {
			System.out.printf("=== RUN %d ===============\n", i);

			resolveArtifactsStartingAtPom("pom.xml").forEach(res -> {
				if (db.contains(res.coordinate) || coordsToSources.containsKey(res.coordinate)) {
					// can be ignored
				} else if (res.source.startsWith("http")) {
					System.out.printf("FOUND SOURCE: %s\n", res.coordinate);
					coordsToSources.put(res.coordinate, res.getPomUrl());
				} else {
					if (i == 0) {
						System.out.printf("DELETE LOCAL: %s\n", res.coordinate);
						if (res.file.exists() && res.file.isFile()) {
							res.file.delete();
						}
					} else {
						System.out.println("NO URL:" + res.coordinate);
						coordsToSources.put(res.coordinate, null);
					}
				}
			});

			System.out.println();
		});

		System.out.println("=== NEWLY DISCOVERED SOURCES ===============");

		coordsToSources.forEach((coord, src) -> {
			System.out.printf("%s\n\t%s\n--\n", coord, src);
//			System.out.printf("\"%s\", //\n", coord);
		});
	}

	private Set<String> initDatabaseWithSomeCooridnates(String... coordinates) {
		return Arrays.stream(coordinates).collect(Collectors.toSet());
	}

	private Set<ResolutionResult> resolveArtifactsStartingAtPom(String pathToPom) {
		var res = new HashSet<ResolutionResult>();
		MavenResolvedArtifactImpl.artifactRepositories = res;
		Maven.resolver() //
				.loadPomFromFile(pathToPom) //
				.importCompileAndRuntimeDependencies() //
				.resolve() //
				.withTransitivity() //
				.asResolvedArtifact();
		MavenResolvedArtifactImpl.artifactRepositories = null;
		return res;
	}
}