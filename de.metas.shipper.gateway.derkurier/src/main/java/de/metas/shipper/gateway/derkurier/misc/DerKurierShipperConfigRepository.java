package de.metas.shipper.gateway.derkurier.misc;

import static org.adempiere.model.InterfaceWrapperHelper.loadOutOfTrx;

import java.util.Optional;

import org.adempiere.ad.dao.IQueryBL;
import org.adempiere.util.Check;
import org.adempiere.util.Services;
import org.compiere.model.I_AD_MailBox;
import org.compiere.util.CCache;
import org.compiere.util.TimeUtil;
import org.springframework.stereotype.Repository;

import de.metas.email.Mailbox;
import de.metas.shipper.gateway.derkurier.model.I_DerKurier_Shipper_Config;

/*
 * #%L
 * de.metas.shipper.gateway.derkurier
 * %%
 * Copyright (C) 2018 metas GmbH
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

@Repository
public class DerKurierShipperConfigRepository
{
	private static CCache<Integer, Optional<DerKurierShipperConfig>> cache = CCache.newCache(
			I_DerKurier_Shipper_Config.Table_Name + "#by#" + I_DerKurier_Shipper_Config.COLUMNNAME_M_Shipper_ID,
			10,
			CCache.EXPIREMINUTES_Never);

	public DerKurierShipperConfig retrieveConfigForShipperIdOrNull(final int shipperId)
	{
		return cache
				.getOrLoad(shipperId, () -> retrieveConfigForShipperId0(shipperId))
				.orElse(null);
	}

	public DerKurierShipperConfig retrieveConfigForShipperId(final int shipperId)
	{
		final Optional<DerKurierShipperConfig> config = cache.getOrLoad(shipperId, () -> retrieveConfigForShipperId0(shipperId));

		return Check.assumePresent(config, "There has to be a DerKurier_Shipper_Config record for shipperId={}", shipperId);
	}

	private Optional<DerKurierShipperConfig> retrieveConfigForShipperId0(final int shipperId)
	{
		Check.assumeGreaterThanZero(shipperId, "shipperId");

		final I_DerKurier_Shipper_Config shipperConfigRecord = Services.get(IQueryBL.class).createQueryBuilder(I_DerKurier_Shipper_Config.class)
				.addOnlyActiveRecordsFilter()
				.addEqualsFilter(I_DerKurier_Shipper_Config.COLUMN_M_Shipper_ID, shipperId)
				.create()
				.firstOnly(I_DerKurier_Shipper_Config.class);
		if (shipperConfigRecord == null)
		{
			Optional.empty();
		}

		final DerKurierShipperConfig shipperConfig = DerKurierShipperConfig.builder()
				.restApiBaseUrl(shipperConfigRecord.getAPIServerBaseURL())
				.customerNumber(shipperConfigRecord.getDK_CustomerNumber())
				.parcelNumberAdSequenceId(shipperConfigRecord.getAD_Sequence_ID())
				.deliveryOrderMailBoxOrNull(loadMailboxOrNull(shipperConfigRecord.getAD_MailBox_ID()))
				.deliveryOrderRecipientEmailOrNull(shipperConfigRecord.getEMail_To())
				.collectorCode(shipperConfigRecord.getCollectorCode())
				.customerCode(shipperConfigRecord.getCustomerCode())
				.desiredTimeFrom(TimeUtil.asLocalTime(shipperConfigRecord.getDK_DesiredDeliveryTime_From()))
				.desiredTimeTo(TimeUtil.asLocalTime(shipperConfigRecord.getDK_DesiredDeliveryTime_To()))
				.build();

		return Optional.of(shipperConfig);
	}

	private Mailbox loadMailboxOrNull(final int mailBoxId)
	{
		if (mailBoxId <= 0)
		{
			// nothing to do
			return null;
		}
		final I_AD_MailBox shipperConfigMailBox = loadOutOfTrx(mailBoxId, I_AD_MailBox.class);

		final Mailbox mailbox = Mailbox.builder()
				.smtpHost(shipperConfigMailBox.getSMTPHost())
				.smtpPort(shipperConfigMailBox.getSMTPPort())
				.startTLS(shipperConfigMailBox.isStartTLS())
				.email(shipperConfigMailBox.getEMail())
				.username(shipperConfigMailBox.getUserName())
				.password(shipperConfigMailBox.getPassword())
				.smtpAuthorization(shipperConfigMailBox.isSmtpAuthorization())
				.adUserId(null)
				.build();
		return mailbox;
	}
}
