/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. 
 * See the copyright.txt in the distribution for a full listing 
 * of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 * (C) 2005-2007,
 * @author JBoss Inc.
 */
package com.arjuna.xts.nightout.services.Restaurant;

import javax.jws.WebService;

@WebService
public interface IRestaurantServiceBA
{
     /**
     * Book a number of seats in the restaurant
     * Enrols a Participant if necessary, then passes
     * the call through to the business logic.
     *
     * @param how_many The number of seats to book
     */
    public boolean bookSeats(int how_many);

}
