package de.metas.handlingunits.inout;

import java.math.BigDecimal;

import org.adempiere.model.IContextAware;

import de.metas.inout.model.I_M_InOutLine;
import de.metas.inout.model.I_M_InOutLine_HU_Alloc;
import de.metas.inoutcandidate.api.IReceiptScheduleAllocBuilder;
import de.metas.inoutcandidate.api.impl.ReceiptScheduleAllocBuilder;
import de.metas.inoutcandidate.model.I_M_ReceiptSchedule_Alloc;

/*
 * #%L
 * de.metas.handlingunits.base
 * %%
 * Copyright (C) 2017 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

public interface IHUCustomerReturnAllocBuilder
{

	I_M_InOutLine_HU_Alloc build();

	I_M_InOutLine_HU_Alloc buildAndSave();

	ReceiptScheduleAllocBuilder setContext(IContextAware context);

	IReceiptScheduleAllocBuilder setQtyToAllocate(BigDecimal qtyToAllocate);

	IReceiptScheduleAllocBuilder setM_InOutLine(I_M_InOutLine receiptLine);
}
