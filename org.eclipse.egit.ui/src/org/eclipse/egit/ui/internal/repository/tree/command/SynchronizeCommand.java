/*******************************************************************************
 * Copyright (c) 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Mathias Kinzler (SAP AG) - initial implementation
 *    Dariusz Luksza <dariusz@luksza.org>
 *******************************************************************************/
package org.eclipse.egit.ui.internal.repository.tree.command;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.egit.core.synchronize.dto.GitSynchronizeData;
import org.eclipse.egit.ui.UIText;
import org.eclipse.egit.ui.internal.repository.tree.RepositoryTreeNode;
import org.eclipse.egit.ui.internal.synchronize.GitSynchronize;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.osgi.util.NLS;

/**
 * Implements "Synchronize"
 */
public class SynchronizeCommand extends
		RepositoriesViewCommandHandler<RepositoryTreeNode> {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		final RepositoryTreeNode node = getSelectedNodes(event).get(0);
		Object object = node.getObject();
		if (!(object instanceof Ref))
			return null;

		final Ref ref = (Ref) object;
		final Repository repo = node.getRepository();
		Job job = new Job(NLS.bind(
				UIText.SelectSynchronizeResourceDialog_selectProject, repo
						.getDirectory())) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				GitSynchronizeData data = new GitSynchronizeData(node
						.getRepository(), Constants.HEAD, ref.getName(), false);

				new GitSynchronize(data);

				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();

		return null;
	}
}